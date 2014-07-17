import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;

import org.junit.Before;
import org.junit.Test;

public class AcceptanceTest {

	private static final String EMPLOYEE_DATA_FILE = "./employee_data.txt";
	private static final int SMTP_PORT = 25;
	private List<Message> messagesSent;
	private BirthdayService service;
	private Emailer emailer;
	
	@Before
	public void setUp() throws Exception {
		new TestDataFile().create(EMPLOYEE_DATA_FILE);
		messagesSent = new ArrayList<Message>();

		emailer = new Emailer("localhost", SMTP_PORT) {
			protected void sendMessage(Message msg) {
				messagesSent.add(msg);
			}
		};
		service = new BirthdayService();
	}

	@Test
	public void sendsMessageForBirthdays() throws Exception {
		service.sendGreetings(EMPLOYEE_DATA_FILE, new OurDate("2008/10/08"), emailer);
		
		assertEquals("message not sent?", 1, messagesSent.size());
		Message message = messagesSent.get(0);
		assertEquals("Happy Birthday, dear John!", message.getContent());
		assertEquals("Happy Birthday!", message.getSubject());
		assertEquals(1, message.getAllRecipients().length);		
		assertEquals("john.doe@foobar.com", message.getAllRecipients()[0].toString());		
	}
	
	@Test
	public void willNotSendEmailsWhenNobodysBirthday() {
		service.sendGreetings(EMPLOYEE_DATA_FILE, new OurDate("2008/01/01"), emailer);
		assertEquals("what? messages?", 0, messagesSent.size());
	}

}
