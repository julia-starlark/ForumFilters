package telran.ashkelon2018.forum.service.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;

@Service
@Order(3)
public class RoleFilter implements Filter {

	@Autowired
	UserAccountRepository repository;
	@Autowired
	AccountConfiguration config;

	@Override
	public void doFilter(ServletRequest reqs, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) reqs;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getRequestURI();
		String method = request.getMethod();
		String token = request.getHeader("Authorization");
		boolean filter0 = !(path.equals("/account") && "POST".equals(method));
		boolean filter1 = !path.startsWith("/forum/posts");
		boolean filter2 = path.matches("/account/.+") && "DELETE".equals(method);
		boolean filter3 = path.matches("/account/.+/.+") && "PUT".equals(method);
		boolean filter4 = path.matches("/forum/post/.+") && "DELETE".equals(method);
		if (filter0 && filter1) {
			//get login of the user to modify
			String login = path.split("/")[2]; 
			//get credentials of the user who wants to do modifications
			AccountUserCredentials creds = config.tokenDecode(token);
			String userLogin = creds.getLogin();
			//get the user who wants to do modifications
			UserAccount user = repository.findById(userLogin).get();
			if (filter2 || filter4) {
				if (!(userLogin.equals(login) || user.getRoles().contains("admin")
						|| user.getRoles().contains("moderator"))) {
					response.sendError(403, "Forbidden");
					return;
				}
			}
			if (filter3) {
				if (!user.getRoles().contains("admin")) {
					response.sendError(403, "Forbidden");
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
