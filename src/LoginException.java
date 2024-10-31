
public class LoginException extends Exception {
	public LoginException() {
        super("로그인 처리 중 예외가 발생했습니다.");
    }

    // 메시지를 받는 생성자
    public LoginException(String message) {
        super(message);
    }

}
