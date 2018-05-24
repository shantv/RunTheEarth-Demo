package run.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import run.model.UserDetail;

public interface UserDetailRepository extends MongoRepository<UserDetail,String> {
	long countByUserId(String userId);
	UserDetail findByUserId(String userId);
	UserDetail findByUsername(String userName);
}
