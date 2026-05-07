package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.scheduling.dto.InstitutionalHolidayRequest;
import bo.edu.uagrm.ugram.scheduling.dto.InstitutionalHolidayResponse;
import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHoliday;
import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHolidayType;
import bo.edu.uagrm.ugram.scheduling.repository.InstitutionalHolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionalHolidayService {

    private final InstitutionalHolidayRepository institutionalHolidayRepository;

    @Transactional(readOnly = true)
    public List<InstitutionalHolidayResponse> getHolidays(LocalDate dateFrom, LocalDate dateTo) {
        List<InstitutionalHoliday> holidays;

        if (dateFrom != null && dateTo != null) {
            if (dateTo.isBefore(dateFrom)) {
                throw new BusinessException("La fecha final no puede ser anterior a la fecha inicial");
            }
            holidays = institutionalHolidayRepository.findByDateBetweenOrderByDateAscStartTimeAsc(dateFrom, dateTo);
        } else if (dateFrom == null && dateTo == null) {
            holidays = institutionalHolidayRepository.findAllByOrderByDateAscStartTimeAsc();
        } else {
            throw new BusinessException("Debes enviar ambas fechas o ninguna para consultar feriados");
        }

        return holidays.stream()
                .sorted(Comparator
                        .comparing(InstitutionalHoliday::getDate)
                        .thenComparing(InstitutionalHoliday::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(this::mapResponse)
                .toList();
    }

    @Transactional
    public InstitutionalHolidayResponse createHoliday(InstitutionalHolidayRequest request) {
        validateRequest(request);
        validateConflicts(request);

        InstitutionalHoliday holiday = InstitutionalHoliday.builder()
                .date(request.getDate())
                .type(request.getType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .reason(request.getReason().trim())
                .build();

        InstitutionalHoliday saved = institutionalHolidayRepository.save(holiday);
        return mapResponse(saved);
    }

    private void validateRequest(InstitutionalHolidayRequest request) {
        if (request.getType() == InstitutionalHolidayType.TOTAL) {
            if (request.getStartTime() != null || request.getEndTime() != null) {
                throw new BusinessException("Un feriado total no debe incluir horario parcial");
            }
            return;
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException("Una jornada parcial requiere hora de inicio y fin");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private void validateConflicts(InstitutionalHolidayRequest request) {
        List<InstitutionalHoliday> sameDateHolidays = institutionalHolidayRepository.findByDate(request.getDate());

        if (request.getType() == InstitutionalHolidayType.TOTAL && !sameDateHolidays.isEmpty()) {
            throw new BusinessException("Ya existe una restricción institucional registrada para esa fecha");
        }

        boolean hasTotalHoliday = sameDateHolidays.stream()
                .anyMatch(holiday -> holiday.getType() == InstitutionalHolidayType.TOTAL);

        if (hasTotalHoliday) {
            throw new BusinessException("La fecha ya está marcada como feriado total");
        }

        if (request.getType() == InstitutionalHolidayType.PARTIAL) {
            boolean overlaps = sameDateHolidays.stream()
                    .filter(holiday -> holiday.getType() == InstitutionalHolidayType.PARTIAL)
                    .anyMatch(holiday -> request.getStartTime().isBefore(holiday.getEndTime())
                            && request.getEndTime().isAfter(holiday.getStartTime()));

            if (overlaps) {
                throw new BusinessException("La jornada parcial se solapa con otra restricción institucional existente");
            }
        }
    }

    private InstitutionalHolidayResponse mapResponse(InstitutionalHoliday holiday) {
        return InstitutionalHolidayResponse.builder()
                .id(holiday.getId())
                .date(holiday.getDate())
                .type(holiday.getType())
                .startTime(holiday.getStartTime())
                .endTime(holiday.getEndTime())
                .reason(holiday.getReason())
                .build();
    }
}
