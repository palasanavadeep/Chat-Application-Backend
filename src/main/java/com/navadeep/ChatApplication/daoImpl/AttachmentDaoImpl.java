package com.navadeep.ChatApplication.daoImpl;


import com.navadeep.ChatApplication.dao.AttachmentDao;
import com.navadeep.ChatApplication.domain.Attachment;
import org.hibernate.SessionFactory;

public class AttachmentDaoImpl extends BaseDaoImpl<Attachment> implements AttachmentDao {

    public AttachmentDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Attachment.class);
    }

}