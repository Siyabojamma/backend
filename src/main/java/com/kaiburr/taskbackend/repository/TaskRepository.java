
package com.kaiburr.taskbackend.repository;

import com.kaiburr.taskbackend.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByNameContainingIgnoreCase(String name);
}
