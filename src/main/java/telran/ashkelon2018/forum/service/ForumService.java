package telran.ashkelon2018.forum.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;

public interface ForumService {
	Post addNewPost(NewPostDto newPost, String token);

	Post getPost(String id);

	Post removePost(String id);

	Post updatePost(PostUpdateDto postUdpate, String token);

	boolean addLike(String id, HttpServletRequest request); // учет лайков

	Post addComment(String id, NewCommentDto newComment, String token);
	
	Iterable<Post> findPostsByTags(List<String> tags);

	Iterable<Post> findPostsByAutor(String author);

	Iterable<Post> findPostsByDates(DatePeriodDto datesDto);
}
