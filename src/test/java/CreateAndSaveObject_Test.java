import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright AdScale GmbH, Germany, 2007
 */
public class CreateAndSaveObject_Test {

    @Test
    public void saveEntityWithoutMandatoryRelations() throws Exception {
        Account account = new Account();
        String expectedName = "Advertiser1";
        account.setName(expectedName);
        new CreateAndSaveObject().save(account);

        assertEquals(expectedName, account.getName());
    }


    @Test
    public void saveEntityWithMandatoryRelations() throws Exception {
        String expectedName = "Slot1";
        Slot slot = new Slot();
        slot.setName(expectedName);
        new CreateAndSaveObject().makeSavable(slot);

        assertNotNull(slot.getWebsite());
    }


    @Test
    public void saveSomeWithNumbers() throws Exception {
        new CreateAndSaveObject().save(new AdSlotDayArchive());
    }


    @Test
    public void saveWithManyToOne() throws Exception {
        new CreateAndSaveObject().save(new AdvertApprovalEmail());
    }


    @Test
    public void saveWithLoop() throws Exception {
        new CreateAndSaveObject().save(new AdvertiserAdvertStats());
    }


    @Test
    public void allHibernateObjectsAreValid() throws Exception {
        testObjects(listOfAllHibernateObjects());
    }

    @Test
    public void demoWithCompositeKeys() throws Exception{
        new CreateAndSaveObject().save(new AccountAdvertReject());
    }

    @Test
    public void demoGet() throws Exception {
        List list = new CreateAndSaveObject().get(Account.class);
        for (Object o : list) {
            System.out.println("o = " + ReflectionToStringBuilder.reflectionToString(o, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    @Test
    public void demoDisplay() throws Exception {
        String display = new CreateAndSaveObject().show(Account.class);
        System.out.println("display = " + display);
    }



    private void testObjects(List<String> include) {
        testObjects(include, new ArrayList<String>());
    }


    private void testObjects(List<String> include, List<String> exclude) {
        removedExcluded(include, exclude);
        String[] strings = new String[include.size()];

        int i = 0;
        for (String string : include) {
            strings[i++] = string;
        }
        testObjects(strings);
    }


    private void removedExcluded(List include, List exclude) {
        for (Object s : exclude) {
            include.remove(s);
        }
    }


    private void testObjects(String... clazzNames) {
        for (String clazzName : clazzNames) {
            String name = clazzName.substring(0, clazzName.indexOf("."));
            System.out.println("clazzName = " + name);

            try {
                Object hibernateObj = Class.forName(name).getConstructor(null).newInstance(null);
                new CreateAndSaveObject().save(hibernateObj);
            }
            catch (Exception e) {
            }
        }
    }


    private List<String> listOfAllHibernateObjects() {
        List<File> files = getFiles();
        List<String> clazzNames = new ArrayList<String>();
        for (File file : files) {
            clazzNames.add(file.getName());
        }
        return clazzNames;
    }


    private List<File> getFiles() {
        List<File> files = null;

        List<File> viewFiles = new ArrayList<File>();
        try {
            files = FileUtils.getFiles(new File("target/generated-sources/hibernate3"), "*.hbm.xml", null);

            listOfViews(files, viewFiles);
        }
        catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        removedExcluded(files, viewFiles);

        return files;
    }


    private void listOfViews(List<File> files, List<File> viewFiles) {
        for (File file : files) {
            String fileName = file.getName();
            if (ifStartsWithVAndSecondLetterIsUpperCase(fileName)) {
                viewFiles.add(file);
            }
        }
    }


    private boolean ifStartsWithVAndSecondLetterIsUpperCase(String fileName) {
        return fileName.charAt(0) == 'V' && Character.isUpperCase(fileName.charAt(1));
    }

}
