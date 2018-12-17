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

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;

@Service
@Order(1) //first in the turn of filters
public class AuthentificationFilter implements Filter {
	
	@Autowired
	UserAccountRepository repository;
	@Autowired
	AccountConfiguration config;

	@Override
	public void doFilter(ServletRequest reqs, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) reqs;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		boolean filter1 = path.startsWith("/account") && !"POST".equals(method); //к этому эндпойнту с методом пост имеют доступ все (зарегистрироваться)
		boolean filter2 = path.startsWith("/forum") && !path.startsWith("/forum/posts"); //все могут посмотреть посты на форуме
		if(filter1 || filter2) {
			String token = request.getHeader("Authorization");
			if(token == null) {
				response.sendError(401, "Unauthorized");
				return;
			}
			AccountUserCredentials userCredentials = null;
			try {
				userCredentials = config.tokenDecode(token);
			} catch (Exception e) {
				response.sendError(401, "Unauthorized");
				return;
			}
			UserAccount userAccount = repository.findById(userCredentials.getLogin()).orElse(null);
			if(userAccount == null) {
				response.sendError(401, "User not found");
				return;
			}else {
				if(!BCrypt.checkpw(userCredentials.getPassword(), userAccount.getPassword())) {
					response.sendError(403, "Forbidden");
					return;
				}
				request.setAttribute("login", userAccount.getLogin());
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
