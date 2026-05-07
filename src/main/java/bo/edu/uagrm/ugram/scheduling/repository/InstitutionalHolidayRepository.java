package bo.edu.uagrm.ugram.scheduling.repository;

import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InstitutionalHolidayRepository extends JpaRepository<InstitutionalHoliday, UUID> {
    List<InstitutionalHoliday> findByDate(LocalDate date);
    List<InstitutionalHoliday> findAllByOrderByDateAscStartTimeAsc();
    List<InstitutionalHoliday> findByDateBetweenOrderByDateAscStartTimeAsc(LocalDate dateFrom, LocalDate dateTo);
}
