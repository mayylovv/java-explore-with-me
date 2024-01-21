package ru.practicum.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.EventState;
import ru.practicum.util.PaginationSetup;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(
            "select e " +
            "from Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "where e.initiator.id = :userId"
    )
    List<Event> findAllWithInitiatorByInitiatorId(Long userId, PaginationSetup paginationSetup);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    // onlyAvailable если false проверяем значение лимит, или равен 0, или согласованных заявок меньше чем лимит.
    @Query(
            "select e " +
            "from Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "where e.state = :state " +
            "and e.eventDate > :rangeStart " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (e.participantLimit = 0 or e.participantLimit > e.confirmedRequests) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (:text is null or (upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (upper(e.description) like upper(concat('%', :text, '%')))" +
            "or (upper(e.title) like upper(concat('%', :text, '%'))))"
    )
    List<Event> findAllPublishStateOnlyAvailable(EventState state, LocalDateTime rangeStart,
                                                 List<Long> categories, Boolean paid,
                                                 String text, PaginationSetup pageable);

    @Query(
             "select e " +
             "from Event AS e " +
             "JOIN FETCH e.initiator " +
             "JOIN FETCH e.category " +
             "where e.state = :state " +
             "and e.eventDate > :rangeStart " +
             "and (:categories is null or e.category.id in :categories) " +
             "and e.participantLimit != 0 " +
             "and e.participantLimit = e.confirmedRequests " +
             "and (:paid is null or e.paid = :paid) " +
             "and (:text is null or (upper(e.annotation) like upper(concat('%', :text, '%'))) " +
             "or (upper(e.description) like upper(concat('%', :text, '%'))))"
             )
    List<Event> findAllPublishStateOnlyNotAvailable(EventState state, LocalDateTime rangeStart,
                                                    List<Long> categories, Boolean paid,
                                                    String text, PaginationSetup pageable);

    @Query(
            "select e " +
                    "from Event AS e " +
                    "JOIN FETCH e.initiator " +
                    "JOIN FETCH e.category " +
                    "where e.eventDate > :rangeStart " +
                    "and (:users is null or e.initiator.id in :users) " +
                    "and (:categories is null or e.category.id in :categories) " +
                    "and (:states is null or e.state in :states)"
    )
    List<Event> findAllForAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart,
                                PageRequest pageable);

    List<Event> findAllByIdIn(Set<Long> events);
}