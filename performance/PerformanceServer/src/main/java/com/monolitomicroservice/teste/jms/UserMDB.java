package com.monolitomicroservice.teste.jms;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monolitomicroservice.teste.performance.common.CallResult;
import com.monolitomicroservice.teste.performance.common.RestResult;
import com.monolitomicroservice.teste.performance.common.UserVO;
import com.monolitomicroservice.teste.session.service.UserService;

@MessageDriven(name = "UserQueue", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "/jms/queue/UserQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class UserMDB implements MessageListener {
    private static final Logger log = Logger.getLogger(UserMDB.class.getName());

    @Inject
    private JMSContext jmsContext;

    @EJB
    private UserService userService;

    @Override
    public void onMessage(Message message) {
        long ini = System.currentTimeMillis();
        try {
            String action = message.getStringProperty("action");
            JMSProducer producer = null;
            TextMessage msgRet = null;
            RestResult r = null;
            switch (action) {
                case "find":
                    Integer start = (Integer) message.getObjectProperty("start");
                    Integer size = (Integer) message.getObjectProperty("size");
                    if (start == null)
                        start = 0;
                    if (size == null)
                        size = 50;

                    CallResult l = userService.find(start, size);

                    producer = jmsContext.createProducer();
                    msgRet = jmsContext.createTextMessage();

                    r = new RestResult(System.currentTimeMillis() - ini,
                            l.getContent(),
                            System.getProperty("jboss.qualified.host.name"));
                    msgRet.setText(new ObjectMapper().writeValueAsString(r));

                    producer.send(message.getJMSReplyTo(), msgRet);

                    log.fine("==== Users found: " + ((List) l.getContent()).size());

                    break;
                case "create":
                    Long tenantId = (Long) message.getObjectProperty("tenantId");
                    String userCode = message.getStringProperty("userCode");
                    String login = message.getStringProperty("login");
                    String password = message.getStringProperty("password");
                    String email = message.getStringProperty("email");
                    String firstName = message.getStringProperty("firstName");
                    String lastName = message.getStringProperty("lastName");
                    String fullName = message.getStringProperty("fullName");
                    Long birthDate = (Long) message.getObjectProperty("birthDate");

                    UserVO t = userCode == null ? new UserVO() : new UserVO(userCode);

                    if (tenantId != null) {
                        t.setTenantId(tenantId);
                    }
                    if (login != null) {
                        t.setLogin(login);
                    }
                    if (password != null) {
                        t.setPassword(password);
                    }
                    if (email != null) {
                        t.setEmail(email);
                    }
                    if (firstName != null) {
                        t.setFirstName(firstName);
                    }
                    if (lastName != null) {
                        t.setLastName(lastName);
                    }
                    if (fullName != null) {
                        t.setFullName(fullName);
                    } else {
                        t.setFullName(t.getFirstName() + " " + t.getLastName());
                    }
                    if (birthDate != null) {
                        t.setBirthDate(new Date(birthDate));
                    }

                    CallResult userResult = userService.create(t);
                    t = (UserVO) userResult.getContent();

                    producer = jmsContext.createProducer();
                    msgRet = jmsContext.createTextMessage();

                    r = new RestResult(System.currentTimeMillis() - ini,
                            t, System.getProperty("jboss.qualified.host.name"));
                    msgRet.setText(new ObjectMapper().writeValueAsString(r));

                    producer.send(message.getJMSReplyTo(), msgRet);

                    log.fine("==== User created: " + t);

                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
