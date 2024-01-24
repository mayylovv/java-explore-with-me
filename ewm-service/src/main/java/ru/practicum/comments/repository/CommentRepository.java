package ru.practicum.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByAuthorId(Long userId, PageRequest page);

    Page<Comment> findAllByEventId(Long eventId, PageRequest page);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long userId);
}
