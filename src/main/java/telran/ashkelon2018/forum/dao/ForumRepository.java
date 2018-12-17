package telran.ashkelon2018.forum.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.ashkelon2018.forum.domain.Post;

public interface ForumRepository extends MongoRepository<Post, String> {
	
	Iterable<Post> findByAuthor(String author);
	
	Iterable<Post> findByDateCreatedBetween(LocalDate from, LocalDate to);

	@Query("{'tags': {'$in': ?0}}")
	Iterable<Post> findPostsByTags(List<String> tags);
	
	//Iterable<Post> findPostsByTagsIn(List<String> tags);
}
