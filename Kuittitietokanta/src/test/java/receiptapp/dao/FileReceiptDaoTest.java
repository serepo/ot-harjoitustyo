package receiptapp.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import receiptapp.domain.HelperFunctions;
import receiptapp.domain.Receipt;
import receiptapp.domain.ReceiptItem;

/**
 *
 * @author resure
 */
public class FileReceiptDaoTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    
    File testFile;
    String testFileName;
    FileReceiptDao testDao;
    ObservableList<Receipt> testReceipts;
    ReceiptItem item;
    Receipt receipt;
    Random rand = new Random();
    
    public FileReceiptDaoTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        testFileName = "testfile_receipts.db";
        testDao = new FileReceiptDao(testFileName);
        testReceipts = testDao.getReceipts();
        testFile = testDao.getFile();
        item = new ReceiptItem("product_name", 10.5, true, 0.5, "kg");
        receipt = new Receipt("store", LocalDate.parse("2020-11-11"), FXCollections.observableArrayList());
    }
   
    @Test
    public void databaseExists() {
        assertTrue(testFile.exists());
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            assertTrue(rs.next());
            assertTrue(rs.next());
            assertTrue(rs.next());
            assertFalse(rs.next());
        } catch (Exception e) {
        }
    }
    
    @Test
    public void createTablesReturnsFalseIfDBIsCorrupted() {
        
    }
    
    @Test
    public void readingEmptyDBReturnsEmptyReceiptList() throws Exception {
        testDao.readReceiptDatabase();
        assertEquals(0, testDao.getReceipts().size());
    }
    
    @Test
    public void readDatabaseCreatesReceiptObjectsToItsAttributeList() throws Exception {
        try (Connection db = getConnection()) {
            PreparedStatement p;
            List<String> stores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                p = db.prepareStatement("INSERT INTO Receipts (store, date) VALUES (?,?);",
                        Statement.RETURN_GENERATED_KEYS);
                String store = "store_" + i;
                p.setString(1, store);
                String date = "2020-12-" + (16-i);
                p.setString(2, date);
                p.executeUpdate();
                
                stores.add(store);
            }
            
            testDao.readReceiptDatabase();
            assertEquals(5, testDao.receipts.size());
            
            for (Receipt r : testDao.receipts) {
                assertTrue(stores.contains(r.getStore()));
            }
            
        } catch (Exception e) {
            
        }
    }

    @Test
    public void readReceiptWithEmptyItemSetReturnEmptyItemList() throws SQLException {
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        Receipt rcpt = new Receipt("store_1", LocalDate.parse("2020-01-01"), items);
        testDao.saveReceipt(rcpt);
        testDao.readReceiptDatabase();
        assertEquals(0, testDao.receipts.get(0).getItems().size());
    }
    
    @Test
    public void readItemsFromDBWithEmptyIdListReturnsEmptyList() throws SQLException {
        assertEquals(0, testDao.readItemsFromDB(new ArrayList()).size());
    }
    
    @Test
    public void readItemsFromDBReturnsItemCorrectObjects() throws Exception {
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        ReceiptItem item1 = new ReceiptItem("prod_1", 10.5, false, 2, "pc");
        ReceiptItem item2 = new ReceiptItem("prod_2", 17.95, true, 0.250111, "kg");
        ReceiptItem item3 = new ReceiptItem("prod_3", 1.45, true, 1.5, "l");
        
        items.add(item1);
        items.add(item2);
        items.add(item3);
        
        testDao.saveNewReceiptItems(items, 1);
        ObservableList<ReceiptItem> DBitems = testDao.readItemsFromDB(new ArrayList(List.of(item1.getId(), item2.getId(), item3.getId())));
        assertEquals(3, DBitems.size());

        Collections.sort(DBitems, (ReceiptItem i1, ReceiptItem i2) -> { 
            return i1.getProduct().compareTo(i2.getProduct());
        });

        ReceiptItem i1 = DBitems.get(0);
        assertEquals(item1.getProduct(), i1.getProduct());
        assertEquals(item1.getPrice(), i1.getPrice(), 0.01);
        assertEquals(item1.getQuantity(), i1.getQuantity(), 0.01);
        assertEquals(item1.getIsUnitPrice(), i1.getIsUnitPrice());
        assertEquals(item1.getUnit(), i1.getUnit());

        ReceiptItem i2 = DBitems.get(1);
        assertEquals("prod_2", i2.getProduct());
        assertEquals(item2.getPrice(), i2.getPrice(), 0.01);
        assertEquals(item2.getQuantity(), i2.getQuantity(), 0.001);
        assertEquals(item2.getIsUnitPrice(), i2.getIsUnitPrice());
        assertEquals(item2.getUnit(), i2.getUnit());

        ReceiptItem i3 = DBitems.get(2);
        assertEquals("prod_3", i3.getProduct());
        assertEquals(item3.getPrice(), i3.getPrice(), 0.01);
        assertEquals(item3.getQuantity(), i3.getQuantity(), 0.001);
        assertEquals(item3.getIsUnitPrice(), i3.getIsUnitPrice());
        assertEquals(item3.getUnit(), i3.getUnit());
    }
    
    @Test
    public void receiptCanBeSavedToDatabase() throws Exception {
        Receipt receipt = new Receipt("store", LocalDate.parse("2020-11-11"), FXCollections.observableArrayList());

        assertTrue(testDao.saveReceipt(receipt));
        Receipt dbReceipt = getReceipt(receipt.getId());
        assertTrue(dbReceipt != null);
        assertEquals(dbReceipt.getId(), receipt.getId());
        assertEquals(dbReceipt.getStore(), receipt.getStore());
        assertEquals(dbReceipt.getDate(), receipt.getDate());
    }
    
    @Test
    public void receiptsCanBeDeleted() throws Exception {
        Receipt receipt = new Receipt("store", LocalDate.parse("2020-11-11"), FXCollections.observableArrayList());
        
        testDao.saveReceipt(receipt);
        assertEquals(1, testDao.deleteReceipt(receipt));
        assertEquals(null, getReceipt(receipt.getId()));
    }
    
    @Test
    public void nothingHappensWhenTryingToDeleteReceiptsNotInDB() throws Exception {
        Receipt receipt = new Receipt("store", LocalDate.parse("2020-11-11"), FXCollections.observableArrayList());
        assertEquals(0, testDao.deleteReceipt(receipt));
    }
    
    @Test
    public void saveReceiptItemsSavesItemsToDB() throws Exception {
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        ReceiptItem randItem1 = getRandomItem();
        ReceiptItem randItem2 = getRandomItem();
        items.add(randItem1);
        items.add(randItem2);

        assertEquals(2, testDao.saveNewReceiptItems(items, 1));
        
        ReceiptItem dbItem1 = getItem(randItem1.getId());
        ReceiptItem dbItem2 = getItem(randItem2.getId());
        
        assertEquals(dbItem1.getId(), randItem1.getId());
        assertEquals(dbItem1.getProduct(), randItem1.getProduct());
        assertEquals(dbItem1.getIsUnitPrice(), randItem1.getIsUnitPrice());
        assertEquals(dbItem1.getQuantity(), randItem1.getQuantity(), 0.01);
        assertEquals(dbItem1.getUnit(), randItem1.getUnit());
        
        assertEquals(dbItem2.getId(), randItem2.getId());
        assertEquals(dbItem2.getProduct(), randItem2.getProduct());
        assertEquals(dbItem2.getIsUnitPrice(), randItem2.getIsUnitPrice());
        assertEquals(dbItem2.getQuantity(), randItem2.getQuantity(), 0.01);
        assertEquals(dbItem2.getUnit(), randItem2.getUnit());
    }
    
    @Test
    public void nothingHappensWhenSavingEmptyItemList() throws Exception {
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        assertEquals(0, testDao.saveNewReceiptItems(items, 1));
    }
    
    @Test
    public void saveNewPurchasesSavesPurchasesToDB() throws Exception {
        testDao.saveNewPurchases(1, 2);
        testDao.saveNewPurchases(1, 4);
        testDao.saveNewPurchases(2, 4);
        
        try (Connection db = getConnection()) {
            PreparedStatement p = db.prepareStatement("SELECT * FROM Purchases WHERE receipt_id=1 AND item_id=2");
            ResultSet pairs = p.executeQuery();
            
            assertEquals(1, pairs.getInt("receipt_id"));
            assertEquals(2, pairs.getInt("item_id"));
            
            p = db.prepareStatement("SELECT * FROM Purchases WHERE receipt_id=1 AND item_id=4;");
            pairs = p.executeQuery();
            
            assertEquals(1, pairs.getInt("receipt_id"));
            assertEquals(4, pairs.getInt("item_id"));
            
            p = db.prepareStatement("SELECT * FROM Purchases WHERE receipt_id=2");
            pairs = p.executeQuery();
            pairs.next();
            assertEquals(2, pairs.getInt("receipt_id"));
            assertEquals(4, pairs.getInt("item_id"));
            assertFalse(pairs.next());
            
            p = db.prepareStatement("SELECT * FROM Purchases WHERE receipt_id=3");
            pairs = p.executeQuery();
            assertFalse(pairs.next());
            
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void purchasesWithIllegalIdsDontGetSaved() throws Exception {                
        assertEquals(-1, testDao.saveNewPurchases(-1, 4));
        assertEquals(-1, testDao.saveNewPurchases(1, -4));
        assertEquals(-1, testDao.saveNewPurchases(1, 0));
    }
    
    @Test
    public void deleteItemsDeletesItemsFromDB() throws Exception {
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        items.add(getRandomItem());
        items.add(getRandomItem());
        items.add(getRandomItem());
        assertEquals(3, testDao.saveNewReceiptItems(items, 1));
        assertEquals(3, testDao.deleteReceiptItems(items));
    }
    
    @Test
    public void nothingHappensWhenTryingToDeleteEmptyItemsList() throws Exception {
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        assertEquals(0, testDao.deleteReceiptItems(items));        
    }    
        
    @Test(expected = SQLException.class)
    public void updateReceiptReturnsFalseIfReceiptNotInDB() throws Exception {
        testDao.updateExistingReceipt(receipt, "store_1", LocalDate.parse("2020-01-01"));
    }
    
    @Test
    public void updateReceiptUpdatesReceiptProperties() throws Exception {
        testDao.saveReceipt(receipt);
        int receiptId = receipt.getId();
        assertTrue(testDao.updateExistingReceipt(receipt, "store_1", LocalDate.parse("2020-01-02")));
        
        try (Connection db = getConnection()) {
            PreparedStatement p = db.prepareStatement("SELECT * FROM Receipts "
                    + "WHERE id=?;");
            p.setInt(1, receiptId);
            ResultSet updatedReceipt = p.executeQuery();
            
            assertEquals("store_1", updatedReceipt.getString("store"));
            assertEquals("2020-01-02", updatedReceipt.getString("date"));
            
        } catch (Exception e) {
            System.out.println("FileReceiptDaoTest.updateReceiptUpdatesReceiptPropertiesWhenItemsListIsEmpty(): " + e);
        }
    }
    
    @Test
    public void updateReceiptUpdatesReceiptPropertiesAndItsItems() throws SQLException {
        ReceiptItem i1 = new ReceiptItem("item_1", 5, true, 0.5, "pc");
        ReceiptItem i2 = new ReceiptItem("item_2", 2.5, false, 1, "kg");
        receipt.addItem(i1);
        receipt.addItem(i2);
        testDao.saveReceipt(receipt);
        
        assertTrue(testDao.updateExistingReceipt(receipt, "store", LocalDate.parse("2020-02-05")));
        assertTrue(testDao.updateExistingItem(i1, "item_1", 700, false, 0.5, "kg"));
        assertTrue(testDao.updateExistingItem(i2, "item_22", 250, false, 0.75, "l"));
        
        PreparedStatement p;
        try (Connection db = getConnection()) {
            p = db.prepareStatement("SELECT * FROM Receipts "
                    + "WHERE id=?");
            p.setInt(1, receipt.getId());
            ResultSet updatedReceipt = p.executeQuery();
            
            assertEquals("store", updatedReceipt.getString("store"));
            assertEquals("2020-02-05", updatedReceipt.getString("date"));
            
            p = db.prepareStatement("SELECT price FROM Items WHERE id=?;");
            p.setInt(1, i1.getId());
            ResultSet updatedItem1 = p.executeQuery();
            assertEquals(700, updatedItem1.getInt("price"));
            
            p = db.prepareStatement("SELECT product FROM Items WHERE id=?;");
            p.setInt(1, i2.getId());
            ResultSet updatedItem2 = p.executeQuery();
            assertEquals("item_22", updatedItem2.getString("product"));
            
            p = db.prepareStatement("SELECT item_id FROM Purchases WHERE receipt_id=?;");
            p.setInt(1, receipt.getId());
            ResultSet itemIds = p.executeQuery();
            int count = 0;
            List<Integer> ids = new ArrayList<>();
            
            while(itemIds.next()) {
                ids.add(itemIds.getInt("item_id"));
                count++;
            }
            
            assertEquals(2, count);
            assertEquals(2, ids.size());
            for (ReceiptItem i : receipt.getItems()) {
                ids.contains(i.getId());
            }
            
        } catch (Exception e) {
            System.out.println("FileReceiptDaoTest.updateReceiptUpdatesReceiptPropertiesAndItsItems(): " + e);
        }
    }
    
    @Test(expected = SQLException.class)
    public void updateItemReturnsFalseIFItemNotInDB() throws SQLException {
        ReceiptItem item = new ReceiptItem("product_1", 110, true, 5, "pc");
        int itemId = item.getId();
        testDao.updateExistingItem(item, "product_1", 550, false, 0.501, "pc");
    }
    
    @Test
    public void updateItemUpdatesExistingItemProperties() throws SQLException {
        ReceiptItem item = new ReceiptItem("product_1", 110, true, 5, "pc");
        ObservableList<ReceiptItem> items = FXCollections.observableArrayList();
        items.add(item);
        testDao.saveNewReceiptItems(items, 1);
        int itemId = item.getId();
        assertTrue(testDao.updateExistingItem(item, "product_1", 550, false, 0.501, "pc"));
        
        try (Connection db = getConnection()) {
            PreparedStatement p = db.prepareStatement("SELECT * FROM Items "
                    + "WHERE id=?;");
            p.setInt(1, itemId);
            ResultSet updatedItem = p.executeQuery();
                        
            assertEquals(updatedItem.getString("product"), "product_1");
            assertEquals(550, updatedItem.getInt("price"));
            assertFalse(updatedItem.getBoolean("is_unit_price"));
            assertEquals(0.501, updatedItem.getDouble("quantity"), 0.001);
            assertEquals("pc", updatedItem.getString("unit"));
            
        } catch (Exception e) {
            System.out.println("FileReceiptDaoTest.updateItemUpdatesItemProperties(): " + e);
        }
    }
    
    @After
    public void tearDown() {
        testFile.delete();
    }
    
    public Receipt getReceipt(int receiptId) {
        Receipt r = null;
        
        try (Connection db = getConnection()) {
            PreparedStatement p = db.prepareStatement("SELECT * FROM Receipts "
                    + "WHERE id=?");
            p.setInt(1, receiptId);
            ResultSet rs = p.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String store = rs.getString("store");
                String date = rs.getString("date");
                r = new Receipt(store, LocalDate.parse(date), FXCollections.observableArrayList());
                r.setId(id);
                return r;
            }
            
        } catch (Exception e) {
            System.out.println("FileReceiptDao.getReceipt(): " + e);
        }
        
        return r;
    }
    
    public ReceiptItem getItem(int itemId) {
        ReceiptItem i = null;
        
        try (Connection db = getConnection()) {
            PreparedStatement p = db.prepareStatement("SELECT * FROM Items "
                    + "WHERE id=?");
            p.setInt(1, itemId);
            ResultSet rs = p.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String product = rs.getString("product");
                int price = rs.getInt("price");
                boolean isUnitPrice = rs.getBoolean("is_unit_price");
                double quantity = rs.getDouble("quantity");
                String unit = rs.getString("unit");
                
                i = new ReceiptItem(product, HelperFunctions.shiftDouble(price, -2), isUnitPrice, quantity, unit);
                i.setId(id);
                return i;
            } else {
            }
            
        } catch (Exception e) {
            System.out.println("FileReceiptDao.getReceipt(): " + e);
        }
        
        return i;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + testFileName);
    }
    
    public ReceiptItem getRandomItem() {
        double price = rand.nextInt(11) + 1 + rand.nextDouble();
        double quantity = rand.nextInt(5) + 1 + rand.nextDouble();
        String product = "product_" + rand.nextInt(51);
        int u = rand.nextInt(3);
        String unit = "pc";
        if (u % 3 == 0) {
            unit = "kg";
        } else if (u % 3 == 1) {
            unit = "l";
        }
        return new ReceiptItem(product, price, rand.nextBoolean(), quantity, unit);
    }
}
