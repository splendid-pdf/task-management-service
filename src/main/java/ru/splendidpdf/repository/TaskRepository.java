package ru.splendidpdf.repository;

import org.springframework.data.repository.CrudRepository;
import ru.splendidpdf.model.Task;

public interface TaskRepository extends CrudRepository<Task, String> {
}