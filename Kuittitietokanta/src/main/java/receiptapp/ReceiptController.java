package receiptapp;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import receiptapp.domain.ReceiptService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import receiptapp.domain.ReceiptItem;
import receiptapp.domain.Receipt;

/**
 * FXML Controller class
 *
 * @author resure
 */
public class ReceiptController implements Initializable {

    private ReceiptService receiptService;
    private ReceiptMain application;
    
    @FXML private TextField storeField;
    @FXML private TextField productField;
    @FXML private TextField priceField;
    @FXML private TextField qntyField;
    @FXML private Label receiptTotal;
    @FXML private DatePicker date;
    @FXML private ChoiceBox<String> unitChoice;
    @FXML private Button cancelBtn;
    @FXML private Button okBtn;
    @FXML private Button addProductBtn;
    @FXML private Button addReceiptBtn;
    @FXML private Button removeReceipt;
    @FXML private Button editItemBtn;
    @FXML private Button deleteItemBtn;
    
    @FXML private TableView<ReceiptItem> itemTable;
    @FXML private TableColumn<ReceiptItem, String> productCol;
    @FXML private TableColumn<ReceiptItem, Double> priceCol;
    @FXML private TableColumn<ReceiptItem, Double> qntyCol;
    @FXML private TableColumn<ReceiptItem, String> unitCol;
    
    @FXML private TableView<Receipt> receiptTable;
    @FXML private TableColumn<Receipt, String> storeCol;
    @FXML private TableColumn<Receipt, LocalDate> dateCol;
    @FXML private TableColumn<Receipt, Integer> productsCol;
    @FXML private TableColumn<Receipt, Double> totalCol;

    
    private final Pattern doublePattern;
    private ReceiptItem selectedItem;
    private Receipt selectedReceipt;
    
    
    public ReceiptController() {
        this.doublePattern = Pattern.compile("[0-9]*\\.[0-9]+|[0-9]+");
    }
    
    public void setApplication(ReceiptMain application) {
        this.application = application;
    }
    public void setReceiptService(ReceiptService receiptService) {
        this.receiptService = receiptService;
        
        this.itemTable.setItems(this.receiptService.getReceiptItems());
        this.receiptTable.setItems(this.receiptService.getReceipts());
        
        List<String> units = this.receiptService.getUnits();
        for (var unit : units) {
            this.unitChoice.getItems().add("" + unit);
        }
        this.unitChoice.setValue(units.get(0));
    }
    
    /**
     * Alustetaan kaikki fxml-jutut ja tehdään tarvittavat asetukset.
     * TODO: jaa tämäkin erillisiin metodeihin jos mahdollista, eli esim.
     * initializeChoseBox(), initializeItemTable(), initializeReceiptTable()
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        this.productCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        this.priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        this.qntyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        this.unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));        
        this.itemTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) 
                        -> enableEditAndRemove(newSelection));
        
        this.storeCol.setCellValueFactory(new PropertyValueFactory<>("store"));
        this.dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.productsCol.setCellValueFactory(new PropertyValueFactory<>("productCount"));
        this.totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        this.receiptTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) ->
                              setSelectedReceipt(newSelection));
        
        //TODO: this.receiptTable.setItems(receiptService:n receiptit), ja sama itemeille
        // VAIN YHDEN KERRAN. tee testi: nappi joka lisää random itemin listaan ja katso päivittyykö tableview  
    }


    @FXML
    void handleAddItem(ActionEvent event) {
        addItem();
        clearAddFields();
    }
    
    @FXML
    void handleOk(ActionEvent event) {
        addReceipt();
        clearAllFields();
    }
        
    @FXML
    void handleEditItem(ActionEvent event) {
        System.out.println("nyt voi muokata");
        
        this.productField.setText(this.selectedItem.getProduct());
        this.priceField.setText("" + this.selectedItem.getPrice());
        this.qntyField.setText("" + this.selectedItem.getQuantity());
        this.unitChoice.setValue(this.selectedItem.getUnit());
        int selectedId = this.selectedItem.getId();
        
    }
    
    
    @FXML
    void handleRemoveReceipt(ActionEvent event) {

    }

    @FXML
    void handleDeleteItem(ActionEvent event) {
        
    }    
    
    @FXML
    void HandleCheckDouble(KeyEvent event) {
        //System.out.println(event.getCharacter());
    }
    
    @FXML
    void handleAddReceipt(ActionEvent event) {
        
    }

    @FXML
    void handleCancel(ActionEvent event) {
        
    }

    /**
     * Lisätään uusi tuote itemTableen. Tarkistetaan ensin onko vaaditut kentät
     * täytetty oikein.
     * TODO: error-dialogi jos ei
     */
    public void addItem() {
        String error = checkAddFields();
        
        if (error.length() > 0) {
            errorDialog(error);
            System.out.println(error);
        } else {
        
            String product = this.productField.getText();
            double price = Double.parseDouble(this.priceField.getText());
            String unit = this.unitChoice.getValue();
            double qnty = Double.parseDouble(this.qntyField.getText());
            
            ReceiptItem i = new ReceiptItem(product, price, qnty, unit);
            this.receiptService.addReceiptItem(i);
            
            
            System.out.println("tuotteet:");
            for (ReceiptItem item : this.itemTable.getItems()) {
                System.out.println(item);
            }
        }        
    }
    
    /**
     * Päivitetään kuitit sisältävä receiptTable. EI tarvitse päivittää koska
     * nyt receiptTableen on tallennettu jo receiptServicen receipts-olio
     */
    public void updateReceiptTable() {

    }
    
    /**
     * Päivitetään tuotetaulukko itemTable uuden tuotteen lisäyksen jälkeen.
     * EI tarvitse päivittää koska itemTableen on on jo tallennettu receiptServicen items-olio
     */
    public void updateItemTableAndTotal() {

    }
    
    /**
     * Tyhjennetään kaikki uuden tuotteen lisäämiseen liittyvät kentät.
     */
    public void clearAddFields() {
        this.productField.setText("");
        this.priceField.setText("");
        this.qntyField.setText("");
    }
    
    /**
     * Kesken!
     * @param selected 
     */
    public void enableEditAndRemove(ReceiptItem selected) {

    }
    
    public void setSelectedReceipt(Receipt selected) {
        //this.selectedReceipt = selected;
    }

    
    /**
     * Lisätään uusi kuitti. Samalla tarkistetaan onko kaikki lisäämiseen
     * vaadittavat kentät täytetty oikein.
     */
    public void addReceipt() {
        String error = checkDemandedFields();
        
        if (error.length() > 0) {
            errorDialog(error);
            System.out.println(error);
        } else {
            String store = this.storeField.getText();
            LocalDate dt = this.date.getValue();
            //ArrayList<ReceiptItem> items = new ArrayList<>(this.itemTable.getItems());
            
            //Receipt receipt = new Receipt(store, dt, items);
            this.receiptService.addReceipt(store, dt);
            
            //updateReceiptTable();
            clearAllFields();
            //updateItemTableAndTotal();
        }
    }
    
    /**
     * Tyhjentää kaikki täytettävät kentät.
     */
    public void clearAllFields() {
        clearAddFields();
        this.storeField.setText("");
        this.date.setValue(null);
    }

    /**
     * Avaa virhedialogin.
     * @param message näytettävä virhe
     */
    public void errorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Oh no!");
        alert.setHeight(200);
        
        TextArea area = new TextArea(message);
        area.setWrapText(true);
        area.setEditable(false);
        
        alert.getDialogPane().setContent(area);
        alert.setResizable(true);
        
        alert.showAndWait();
    }    
    
    /**
     * Tarkistetaan kaikki tuotteen lisäämiseen liittyvien kenttien oikeellisuus.
     * @return error-viesti merkkijonona. Tyhjä jos kaikki ok
     */
    public String checkAddFields() {
        String e = "";
        
        if ("".equals(this.productField.getText())) {
            e += "product name cannot be blank\n";
        }
        
        if (!this.doublePattern.matcher(this.priceField.getText()).matches()) {
            e += "price must be a number\n";
        }
        
        if (!this.doublePattern.matcher(this.qntyField.getText()).matches()) {
            e += "quantity must be a number\n";
        }
        
        return e;
    }
    
    /**
     * Tarkistetaan kaikki kuitin lisäämiseen liittyvien kenttien oikeellisuus.
     * @return error-viesti merkkijonona. Tyhjä jos kaikki ok
     */
    public String checkDemandedFields() {
        String e = "";
        
        if ("".equals(this.storeField.getText())) {
            e += "store name cannot be blank\n";
        }
        
        if (this.date.getValue() == null) {
            e += "date cannot be blank\n";
        } else if (this.date.getValue().isAfter(LocalDate.now())) {
            e += "date cannot be in the future\n";
        }
        
        if (this.itemTable.getItems().isEmpty()) {
            e += "receipt must have at least one product";
        }
        
        return e;
    }
}
