# Despliegue del Backend en AWS VPC

## 1. Objetivo

Esta guía deja el backend de **UAGRM Health** corriendo en AWS dentro de una VPC, usando la forma más simple compatible con el proyecto actual:

- una VPC propia
- una subred pública
- una instancia EC2 Ubuntu
- Docker Engine + Docker Compose
- PostgreSQL, MinIO y backend levantados con el `docker-compose` actual del repo
- `Nginx` como reverse proxy
- `HTTPS` para que el frontend en Vercel no falle por mixed content

## 2. Punto crítico antes de empezar

El frontend en Vercel sale por `https://...`.

Por eso el backend **no** debe quedar expuesto solo como:

```text
http://IP_PUBLICA:8080
```

Si haces eso, el navegador bloqueará las llamadas desde Vercel por contenido mixto.

La salida correcta es exponer el backend con un dominio propio y `HTTPS`, por ejemplo:

```text
https://api.tu-dominio.com
```

## 3. Arquitectura mínima recomendada

Para este repo, la arquitectura mínima y coherente es:

```text
Internet
  |
  | HTTPS
  v
Route 53 o DNS externo
  |
  v
Elastic IP -> EC2 Ubuntu
               |- Nginx :80/:443
               |- Backend Spring Boot :8080
               |- PostgreSQL Docker :5432 interno
               |- MinIO Docker :9000 interno
```

## 4. Qué vas a necesitar

- una cuenta AWS
- una key pair para EC2
- un dominio o subdominio para el backend
- acceso SSH a la instancia
- este repositorio disponible en la instancia

## 5. Referencias oficiales

Fuentes usadas para esta guía:

- AWS VPC: https://docs.aws.amazon.com/vpc/latest/userguide/create-vpc.html
- AWS Security Groups: https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-security-groups.html
- Docker Engine en Ubuntu: https://docs.docker.com/installation/ubuntulinux/
- Docker Compose plugin: https://docs.docker.com/compose/install/linux/

## 6. Paso 1 - Crear la VPC

En AWS Console:

1. Ir a `VPC`.
2. Elegir `Create VPC`.
3. Usar `VPC and more`.
4. Configurar algo como:

```text
Name: ugram-health-vpc
IPv4 CIDR: 10.0.0.0/16
Availability Zones: 1
Public subnets: 1
Private subnets: 0
NAT gateways: None
VPC endpoints: None
```

Resultado esperado:

- 1 VPC
- 1 subred pública
- Internet Gateway asociado
- route table con salida a internet

## 7. Paso 2 - Crear Security Group

Crear un security group para la EC2, por ejemplo:

```text
Name: ugram-health-ec2-sg
Description: Backend UAGRM Health
VPC: ugram-health-vpc
```

Inbound recomendado:

- `SSH` puerto `22` desde **tu IP pública**, no desde `0.0.0.0/0`
- `HTTP` puerto `80` desde `0.0.0.0/0`
- `HTTPS` puerto `443` desde `0.0.0.0/0`

Opcional temporal para pruebas:

- `TCP 8080` desde tu IP pública

No abras públicamente:

- `5433`
- `9000`
- `9001`

Aunque Docker los publique en host, si el security group no los abre, no quedan accesibles desde internet.

## 8. Paso 3 - Crear la instancia EC2

Ir a `EC2 -> Launch instance`.

Configuración sugerida:

```text
Name: ugram-health-backend
AMI: Ubuntu Server 24.04 LTS o 22.04 LTS
Instance type: t3.small mínimo
Storage: 20 a 30 GB
Network: ugram-health-vpc
Subnet: la subred pública creada
Auto-assign public IP: enabled
Security group: ugram-health-ec2-sg
Key pair: tu clave SSH
```

Si piensas mantener una IP fija:

1. Crear un `Elastic IP`.
2. Asociarlo a la instancia.

## 9. Paso 4 - Conectarte por SSH

Desde tu máquina:

```bash
chmod 400 tu-clave.pem
ssh -i tu-clave.pem ubuntu@TU_IP_PUBLICA
```

## 10. Paso 5 - Instalar paquetes base

En la instancia:

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y ca-certificates curl gnupg lsb-release git nginx
```

## 11. Paso 6 - Instalar Docker Engine

Basado en Docker Docs para Ubuntu.

```bash
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Agregar tu usuario al grupo docker:

```bash
sudo usermod -aG docker ubuntu
newgrp docker
```

Verificar:

```bash
docker --version
docker compose version
```

## 12. Paso 7 - Clonar el proyecto

Por ejemplo:

```bash
cd /home/ubuntu
git clone TU_REPOSITORIO_GIT backendUniHealth
cd backendUniHealth
```

Si no usarás git remoto, puedes copiar el proyecto por `scp` o subirlo manualmente.

## 13. Paso 8 - Crear variables de entorno

Crear un archivo `.env` en la raíz del repo:

```bash
nano .env
```

Contenido base recomendado:

```dotenv
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

POSTGRES_DB=ugram_health
POSTGRES_USER=ugram
POSTGRES_PASSWORD=CAMBIA_ESTA_PASSWORD

MINIO_ROOT_USER=ugram-admin
MINIO_ROOT_PASSWORD=CAMBIA_ESTA_PASSWORD

JWT_SECRET=CAMBIA_ESTE_SECRETO_CON_UNO_LARGO_Y_REAL

MAIL_USERNAME=TU_CORREO_SMTP
MAIL_PASSWORD=TU_PASSWORD_O_APP_PASSWORD

CORS_ALLOWED_ORIGINS=https://ugram-health-a5uu6r69i-daswitezs-projects.vercel.app,https://ugram-health-web.vercel.app
```

Notas:

- `CORS_ALLOWED_ORIGINS` ya quedó soportado en backend.
- Aunque dejé esos dominios también por defecto, en AWS conviene declararlos explícitamente.
- Usa secretos reales, no los defaults del repo.

## 14. Paso 9 - Levantar el stack con Docker Compose

Desde la raíz del repo:

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

Verificar:

```bash
docker compose -f docker/docker-compose.yml ps
docker compose -f docker/docker-compose.yml logs -f backend
```

La API debería responder internamente en:

```text
http://localhost:8080/api/v1
```

Probar desde la instancia:

```bash
curl http://localhost:8080/api/actuator/health
```

o si usas el context path con endpoints de negocio:

```bash
curl http://localhost:8080/api/v1/auth/login
```

## 15. Paso 10 - Configurar Nginx como reverse proxy

Crear archivo de sitio:

```bash
sudo nano /etc/nginx/sites-available/ugram-health
```

Contenido:

```nginx
server {
    listen 80;
    server_name api.tu-dominio.com;

    client_max_body_size 25m;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Activar:

```bash
sudo ln -s /etc/nginx/sites-available/ugram-health /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## 16. Paso 11 - Apuntar tu dominio al servidor

En tu DNS, crea un registro:

```text
api.tu-dominio.com -> Elastic IP de la EC2
```

No sigas al siguiente paso hasta que ese dominio resuelva correctamente.

## 17. Paso 12 - Habilitar HTTPS

Instalar certbot:

```bash
sudo apt install -y certbot python3-certbot-nginx
```

Emitir certificado:

```bash
sudo certbot --nginx -d api.tu-dominio.com
```

Verificar renovación:

```bash
sudo systemctl status certbot.timer
```

Después de esto, tu backend debería quedar accesible así:

```text
https://api.tu-dominio.com/api/v1
```

## 18. Paso 13 - Validar CORS desde frontend

Con `CORS_ALLOWED_ORIGINS` configurado, el backend debe aceptar:

- `https://ugram-health-a5uu6r69i-daswitezs-projects.vercel.app`
- `https://ugram-health-web.vercel.app`

Puedes probar un preflight desde la instancia o desde tu máquina:

```bash
curl -i -X OPTIONS 'https://api.tu-dominio.com/api/v1/auth/login' \
  -H 'Origin: https://ugram-health-web.vercel.app' \
  -H 'Access-Control-Request-Method: POST'
```

Debes ver headers CORS válidos en la respuesta.

## 19. Paso 14 - Dejarlo corriendo al reiniciar

Como el `docker-compose` tiene:

```text
restart: unless-stopped
```

los contenedores deberían volver a subir tras reinicio del host, siempre que Docker arranque con el sistema.

Verifica:

```bash
sudo systemctl enable docker
sudo systemctl start docker
```

Si quieres reforzarlo, puedes crear un servicio systemd que haga `docker compose up -d` al boot.

Archivo:

```bash
sudo nano /etc/systemd/system/ugram-health.service
```

Contenido:

```ini
[Unit]
Description=UAGRM Health Docker Compose Stack
Requires=docker.service
After=docker.service network-online.target

[Service]
Type=oneshot
WorkingDirectory=/home/ubuntu/backendUniHealth
ExecStart=/usr/bin/docker compose -f docker/docker-compose.yml up -d
ExecStop=/usr/bin/docker compose -f docker/docker-compose.yml down
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
```

Activar:

```bash
sudo systemctl daemon-reload
sudo systemctl enable ugram-health
sudo systemctl start ugram-health
```

## 20. Paso 15 - Operación básica

Ver contenedores:

```bash
docker compose -f docker/docker-compose.yml ps
```

Ver logs del backend:

```bash
docker compose -f docker/docker-compose.yml logs -f backend
```

Rebuild tras cambios:

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

## 21. Seguridad mínima recomendada

- restringir `SSH` solo a tu IP
- no abrir `5433`, `9000` ni `9001` públicamente
- usar `JWT_SECRET` real y largo
- usar credenciales reales para Postgres y MinIO
- no dejar SMTP con credenciales de prueba
- usar `HTTPS` siempre para el backend público

## 22. Qué mejora dejaría para una siguiente iteración

La guía de arriba funciona bien para una primera puesta en producción simple. Después convendría migrar a algo más robusto:

- RDS para PostgreSQL
- S3 en lugar de MinIO si decides salir del modo self-hosted
- subred privada para base de datos
- ALB + ACM en lugar de Nginx local
- CI/CD de despliegue

## 23. Resumen corto de ejecución

Orden operativo:

1. crear VPC
2. crear security group
3. lanzar EC2
4. instalar Docker y Nginx
5. clonar repo
6. crear `.env`
7. levantar `docker compose`
8. apuntar DNS
9. activar `HTTPS`
10. validar CORS desde Vercel
