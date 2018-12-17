package telran.ashkelon2018.forum.service;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.exception.AccessDeniedException;
import telran.ashkelon2018.forum.exceptions.PostNotFoundException;

@Service
public class ForumServiceImpl implements ForumService {
	@Autowired
	ForumRepository repository;

	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration config;

	@Override
	public Post addNewPost(NewPostDto newPost, String token) {
		AccountUserCredentials creds = config.tokenDecode(token);
		UserAccount user = userRepository.findById(creds.getLogin()).get();
		String author = user.getFirstName() + " " + user.getLastName();
		Post post = new Post(newPost.getTitle(), newPost.getContent(), author, newPost.getTags());
		return repository.save(post);
	}

	@Override
	public Post getPost(String id) {
		Post post = repository.findById(id).orElse(null);
		if (post == null) {
			throw new PostNotFoundException();
		}
		return post;
	}

	@Override
	public Post removePost(String id) {
		Post post = getPost(id);
		repository.deleteById(id);
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdate, String token) {
		Post post = getPost(postUpdate.getId());
		AccountUserCredentials creds = config.tokenDecode(token);
		UserAccount user = userRepository.findById(creds.getLogin()).get();
		String userAuthor = user.getFirstName() + " " + user.getLastName();
		String author = post.getAuthor();
		if (author.equals(userAuthor)) {
			String newContent = postUpdate.getContent();
			String newTitle = postUpdate.getTitle();
			if (newContent != null) {
				post.setContent(newContent);
			}
			if (newTitle != null) {
				post.setTitle(newTitle);
			}
			if (postUpdate.getTags() != null) {
				postUpdate.getTags().forEach(t -> post.addTag(t));
			}
		} else {
			throw new AccessDeniedException();
		}
		repository.save(post);
		return post;
	}

	@Override
	public boolean addLike(String id, HttpServletRequest request) {
		Post post = getPost(id);
		String login = (String) request.getAttribute("login");
		if (post.getUserLikes().contains(login)) {
			return false;
		}
		post.addLike();
		post.getUserLikes().add(login);
		repository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment, String token) {
		Post post = getPost(id);
		AccountUserCredentials creds = config.tokenDecode(token);
		Comment comment = new Comment(creds.getLogin(), newComment.getMessage());
		post.addComment(comment);
		repository.save(post);
		return post;
	}

	@Override
	public Iterable<Post> findPostsByTags(List<String> tags) {
		return repository.findPostsByTags(tags);
	}

	@Override
	public Iterable<Post> findPostsByAutor(String author) {
		return repository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto datesDto) {
		LocalDate from = datesDto.getFrom();
		LocalDate to = datesDto.getTo();
		return repository.findByDateCreatedBetween(from, to);
	}

}
