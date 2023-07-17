package spring.angular.social.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChatNotFoundException extends RuntimeException {

private String message;
	
	public ChatNotFoundException() {}
	
	public ChatNotFoundException(String msg)
	{
		super(msg);
		this.message = msg;
	}
}
