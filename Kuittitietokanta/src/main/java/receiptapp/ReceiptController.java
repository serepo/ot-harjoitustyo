package receiptapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import receiptapp.domain.ReceiptService;

/**
 * FXML Controller class
 *
 * @author resure
 */
public class ReceiptController implements Initializable {

    private ReceiptService receiptService;
    private Main application;
    
    public void setApplication(Main application) {
        this.application = application;
    }
    public void setReceiptService(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
