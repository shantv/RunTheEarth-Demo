package run.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import run.model.ActivityType;

public interface ActivityRepository extends MongoRepository<ActivityType,String> {
	ActivityType findByActivityId(String activityId);
}
