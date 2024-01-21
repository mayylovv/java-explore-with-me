package ru.practicum.compilations.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.util.PaginationSetup;
import ru.practicum.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findAllByPinned(Boolean pinned, PaginationSetup paginationSetup);
}