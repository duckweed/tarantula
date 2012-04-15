import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import java.io.File;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

/**
 * Copyright AdScale GmbH, Germany, 2007
 */
public class CreateAndSaveObject {

    private String PROPERTIES_THAT_ARE_NOT_NULLABLE = "//column[@not-null='true']/..";

    private final String PASSWORD_FIELD = "//property[@name='password']";

    static Session session = null;


    public void save(Object obj) throws Exception {
        makeSavable(obj);
        session().saveOrUpdate(obj);
        session().flush();
        session().createSQLQuery("commit").executeUpdate();
        //        session().close();
    }


    public void makeSavable(Object obj) throws Exception {
        SAXReader reader = new SAXReader();
        Document doc = null;
        doc = reader.read(new File("./target/generated-sources/hibernate3/" + obj.getClass().getName() + ".hbm.xml"));
        notNullableFieldsAreSetToDefault(obj, doc);
        ifHasPasswordFieldSetPassword(obj, doc);

        List<Element> list = doc.selectNodes("//composite-id");
        if (!list.isEmpty()) {
            for (Element element : list) {
                String clazz = element.attribute("class").getValue();
                Object o = createObjectWithDefCstr(clazz);
                setProperty(obj, "id", o);
            }
        }
    }


    private static Session session() {
        if (session == null) {
            SessionFactory sessionFactory =
                    new Configuration().configure().addDirectory(new File("./target/generated-sources/hibernate3")).buildSessionFactory();
            session = sessionFactory.openSession();
            session.createSQLQuery("set autocommit to on").executeUpdate();
        }
        return session;
    }


    private byte[] password() throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update("password".getBytes());
        return md.digest();
    }


    private void notNullableFieldsAreSetToDefault(Object obj, Document doc) throws Exception {
        List<Element> list = doc.selectNodes(PROPERTIES_THAT_ARE_NOT_NULLABLE);

        int cnt = 0;
        for (Element element : list) {
            Attribute name1 = elementName(element);
            if (name1 == null) {
                continue;
            }

            System.out.println("checking: " + obj.getClass().getName() + ":" + name1);

            String name = name1.getValue();
            Attribute typeAttr = element.attribute("type");
            if (typeAttr == null) {
                typeAttr = element.attribute("class");
            }
            if (typeAttr == null) {
                continue;
            }
            String type = typeAttr.getValue();

            if (noActionRequired(obj, name)) {
                continue;
            }

            if ("string".equals(type)) {
                setProperty(obj, name, "");
                continue;
            }
            if ("timestamp".equals(type)) {
                setProperty(obj, name, new Date());
                continue;
            }
            if ("big_decimal".equals(type)) {
                setProperty(obj, name, BigDecimal.ZERO);
                continue;
            }

            Attribute aClassAttr = element.attribute("class");
            if (aClassAttr != null) {
                String aClass = aClassAttr.getValue();
                try {
                    Object o = createObjectWithDefCstr(aClass);
                    save(o);
                    BeanUtils.setProperty(obj, name, o);
                }
                catch (Exception e) {
                }
            }

            cnt++;
            System.out.println("cnt = " + cnt);
        }
    }


    private void setProperty(Object bean, String name, Object value) throws Exception {
        BeanUtils.setProperty(bean, name, value);
    }


    private Object createObjectWithDefCstr(String aClass) throws Exception {
        return Class.forName(aClass).getConstructor(null).newInstance(null);
    }


    private Attribute elementName(Element element) {
        Attribute name1 = element.attribute("name");
        return name1;
    }


    private boolean noActionRequired(Object obj, String name) throws Exception {
        return (BeanUtils.getProperty(obj, name) != null);
    }


    private void ifHasPasswordFieldSetPassword(Object obj, Document document) throws Exception {
        if (document.selectNodes(PASSWORD_FIELD).size() < 1) {
            return;
        }
        setProperty(obj, "password", password());
    }


    public List get(Class clazz) {
        return session().createCriteria(clazz).list();
    }


    public String show(Class clazz) {
        String ret = "";
        List list = get(clazz);
        for (Object o : list) {
            ret += ReflectionToStringBuilder.toString(o, ToStringStyle.MULTI_LINE_STYLE) + "\n";
        }

        return ret;
    }
}
