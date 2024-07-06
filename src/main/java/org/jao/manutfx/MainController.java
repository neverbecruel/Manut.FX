package org.jao.manutfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;

public class MainController {

    @FXML
    private TextField numAgInput;
    @FXML
    private ComboBox<String> tipoAgInput;
    @FXML
    private ComboBox<String> talaoAgInput;
    @FXML
    private DatePicker dataInput;
    @FXML
    private Button novaReqButton;
    @FXML
    private VBox plotPanel;

    private static final String DATABASE = "jdbc:sqlite:database1.db";

    @FXML
    public void initialize() {
        tipoAgInput.getItems().addAll("Groz-Beckert", "Neetex");
        talaoAgInput.getItems().addAll("Alto", "Baixo");
        createDatabaseAndTable();
        loadData();
    }

    @FXML
    private void insertData() {
        String quantity = numAgInput.getText();
        String type = tipoAgInput.getValue();
        String talao = talaoAgInput.getValue();
        LocalDate date = dataInput.getValue();

        if (quantity.isEmpty() || type == null || talao == null || date == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Preencha todos os campos", null);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DATABASE)) {
            String sql = "INSERT INTO requisicoes (quantidade, tipo, talao, data) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(quantity));
            pstmt.setString(2, type);
            pstmt.setString(3, talao);
            pstmt.setDate(4, java.sql.Date.valueOf(date));
            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Dados inseridos no banco de dados", null);
            numAgInput.clear();
            tipoAgInput.setValue(null);
            talaoAgInput.setValue(null);
            dataInput.setValue(null);
            loadData();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao inserir os dados no banco de dados", ex.getMessage());
        }
    }

    private void createDatabaseAndTable() {
        try (Connection conn = DriverManager.getConnection(DATABASE)) {
            String sql = "CREATE TABLE IF NOT EXISTS requisicoes " +
                    "(id INTEGER PRIMARY KEY, quantidade INTEGER, tipo TEXT, data DATE, talao TEXT)";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao criar o banco de dados ou a tabela", ex.getMessage());
        }
    }

    private void loadData() {
        plotPanel.getChildren().clear();
        plotPanel.getChildren().add(createPlot("Neetex"));
        plotPanel.getChildren().add(createPlot("Groz-Beckert"));
    }

    private Label createPlot(String tipo) {
        return new Label("Plot for " + tipo);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
