package ru.practicum.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.enums.EventState;
import ru.practicum.util.PaginationSetup;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    Long countByCategoryId(Long id);

    List<Event> findAllWithInitiatorByInitiator_Id(Long userId, PaginationSetup paginationSetup);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("select e " +
                    "from Event AS e " +
                    "JOIN FETCH e.initiator " +
                    "JOIN FETCH e.category " +
                    "where e.state = :state " +
                    "and e.eventDate > :rangeStart " +
                    "and (:categories is null or e.category.id in :categories) " +
                    "and (:paid is null or e.paid = :paid) " +
                    "and (:text is null or (upper(e.annotation) like upper(concat('%', :text, '%'))) " +
                    "or (upper(e.description) like upper(concat('%', :text, '%')))" +
                    "or (upper(e.title) like upper(concat('%', :text, '%'))))"
    )
    List<Event> findAllPublishStateOnlyNotAvailable(EventState state, LocalDateTime rangeStart,
                                                    List<Long> categories, Boolean paid,
                                                    String text, PaginationSetup pageable);

    @Query("SELECT DISTINCT e " +
                    "FROM Event AS e " +
                    "JOIN FETCH e.initiator " +
                    "JOIN FETCH e.category " +
                    "WHERE e.state = :state " +
                    "AND e.eventDate > :rangeStart " +
                    "AND (:categories IS NULL OR e.category.id IN :categories) " +
                    "AND (:paid IS NULL OR e.paid = :paid) " +
                    "AND ( " +
                    "   (:text IS NULL OR UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%'))) " +
                    "   OR UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')) " +
                    "   OR UPPER(e.title) LIKE UPPER(CONCAT('%', :text, '%')) " +
                    ") " +
                    "AND ( " +
                    "   COALESCE((SELECT COUNT(r.id) FROM ParticipationRequest r WHERE r.event = e AND r.status = 'CONFIRMED'), 0) < e.participantLimit " +
                    "   OR e.participantLimit = 0 " +
                    ") " +
                    "ORDER BY e.createdOn DESC"
    )
    List<Event> findAllPublishStateOnlyAvailable(
            EventState state, LocalDateTime rangeStart,
            List<Long> categories, Boolean paid,
            String text, PaginationSetup pageable
    );

    @Query(
            "select e " +
                    "from Event AS e " +
                    "JOIN FETCH e.initiator " +
                    "JOIN FETCH e.category " +
                    "where e.eventDate > :rangeStart " +
                    "and (e.eventDate < :rangeEnd) " +
                    "and (:users is null or e.initiator.id in :users) " +
                    "and (:categories is null or e.category.id in :categories) " +
                    "and (:states is null or e.state in :states)"
    )
    List<Event> findAllForAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                PageRequest pageable);

    List<Event> findAllByIdIn(Set<Long> events);

    @Query("select e from Event as e " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) or :text is null) " +
            "and e.state = :state " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and e.eventDate >= :rangeStart")
    List<Event> getEventsSort(@Param("text") String text,
                              @Param("state") EventState state,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              Pageable pageable);
}
