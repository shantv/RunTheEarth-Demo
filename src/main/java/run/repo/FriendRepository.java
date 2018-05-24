package run.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import run.model.Friend;

public interface FriendRepository extends MongoRepository<Friend,String> {
	List<Friend> findByUserId(String userId);
}
