package telran.ashkelon2018.forum.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PostUpdateDto {
	String id;
	String title;
	String content;
	List<String> tags;
}
