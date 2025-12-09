package com.navadeep.ChatApplication.test;

import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class LoadTestDataGenerator {

    private SessionFactory sessionFactory;
    private LookupService lookupService;

    private Log log = LogFactory.getLog(LoadTestDataGenerator.class);

    // Injected through setter in XML
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }


    /** ------------------------------------------------------------------
     *  Public method you will call manually to generate test data.
     *  ------------------------------------------------------------------ */
    public void generate() {

        log.info("Starting test data generation...");

        createUsers();
        createGroupConversations();

        log.info("Load test data created successfully!");
    }


    // ============================================================
    //  CREATE USERS
    // ============================================================
    private void createUsers() {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        for (int i = 1; i <= 2000; i++) {

            User u = new User();
            u.setUsername("user" + i);
            u.setDisplayName("User " + i);
            u.setEmail("user" + i + "@gmail.com");
            u.setPassword("password" + i);
            u.setCreatedAt(System.currentTimeMillis());
            u.setStatus(true);
            u.setLastSeenAt(System.currentTimeMillis());

            session.merge(u);

            if (i % 25 == 0) {
                session.flush();
                session.clear();
            }
        }

        tx.commit();
        session.close();

        System.out.println("✔ 2000 Users created");
    }


    // ============================================================
    //  CREATE GROUP CONVERSATIONS
    // ============================================================
    private void createGroupConversations() {

        Session session = sessionFactory.openSession();
        log.info("Creating group conversations...");
        List<UserLite> allUsers = session
                .createQuery("FROM UserLite", UserLite.class)
                .list();
        log.info("Users fetched successfully. Total users: " + allUsers.size());
        Lookup groupType = lookupService.findByLookupCode(Constants.CONVERSATION_TYPE_GROUP);
        Lookup adminRole = lookupService.findByLookupCode(Constants.ROLE_ADMIN);
        Lookup memberRole = lookupService.findByLookupCode(Constants.ROLE_MEMBER);

        log.info("fetched lookups successfully.");

        Transaction tx = session.beginTransaction();

        for (int i = 1; i <= 50; i++) {

            UserLite creator = allUsers.get(i - 1);

            // Create conversation
            Conversation c = new Conversation();
            c.setType(groupType);
            c.setCreatedAt(System.currentTimeMillis());
            c.setName("Group Conversation " + i);
            c.setDescription("Load test group " + i);
            c.setCreatedBy(creator);

            List<ConversationParticipant> participants = new ArrayList<>();
            // Add all 1000 users as participants
            for (UserLite user : allUsers) {

                ConversationParticipant cp = new ConversationParticipant();
                cp.setUser(user);
                cp.setCreatedAt(System.currentTimeMillis());
                cp.setRole(user.getId().equals(creator.getId())
                        ? adminRole
                        : memberRole
                );
                cp.setIsMuted(false);
                cp.setIsPinned(false);

                participants.add(cp);
            }

            c.setConversationParticipants(participants);

            session.persist(c);

            session.flush();
            session.clear();

        }

        tx.commit();
        session.close();

        log.info("✔ 100 conversations created with 1000 members each");
    }
}
