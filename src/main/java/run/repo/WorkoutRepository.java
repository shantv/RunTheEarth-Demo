package run.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import run.model.Workout;
import run.model.WorkoutRoute;

public interface WorkoutRepository extends MongoRepository<Workout,String> {
	int countByUserId(String userId);
	long countByReferenceKey(String ref);
	List<Workout> findByUserId(String userId);
	List<Workout> findByUserId(String userId, Pageable page);
}
