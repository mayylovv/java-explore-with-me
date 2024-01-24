package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.requests.EventRequestStatus;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestShort;


import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);

    Optional<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndIdIn(Long eventId, Set<Long> requestIds);

    @Query(value = "SELECT COUNT(*) " +
            "FROM requests " +
            "WHERE event_id = ?1 and status = 'CONFIRMED'", nativeQuery = true)
    Integer getConfirmedRequestsByEventId(Long eventId);

    @Query(
            "select new ru.practicum.requests.model.RequestShort(pr.event.id, count(pr.id)) " +
                    "from Request as pr " +
                    "where pr.event.id in :eventIds " +
                    "and pr.status = :eventRequestStatus " +
                    "group by pr.event.id"
    )
    List<RequestShort> findByEventIdInAndStatus(Set<Long> eventIds, EventRequestStatus eventRequestStatus);
}
