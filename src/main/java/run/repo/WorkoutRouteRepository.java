package run.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import run.model.WorkoutRoute;

public interface WorkoutRouteRepository extends MongoRepository<WorkoutRoute,String> {
	long countByRouteId(String routeId);
	int countByUserId(String userId);
	List<WorkoutRoute> findByUserId(String userId);
	List<WorkoutRoute> findByUserId(String userId, Pageable page);
}
