package telran.ashkelon2018.forum.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NewPostDto {
	@NonNull String title;
	@NonNull String content;
	@NonNull String author;
	@Singular Set<String> tags;
}
