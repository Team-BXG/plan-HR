
package com.example.javaassi;

import com.example.javaassi.db.Database;
import com.example.javaassi.auth.LoginService;
import com.example.javaassi.ui.LoginView;




import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;

import static com.example.javaassi.util.StyleUtil.loginButtonStyle;
import static com.example.javaassi.util.StyleUtil.textFieldStyle;
import static java.sql.DriverManager.getConnection;


public class MainApp extends Application {
    private String currentUsername;// or use int currentUserId if your IDs are numeric
    private TableView<Map<String, String>> employeeTable = new TableView();
    private Stage mainStage;
    private Connection conn;







    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

            try {
                // Load the JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection
                conn = Database.getConnection();

                // If we get here, connection was successful
                System.out.println("Database connection successful!");

                // Rest of your initialization code...
                mainStage = primaryStage;
                primaryStage.setTitle("Employee Management System");
                LoginView.show(mainStage, this::handleLogin);


            } catch (ClassNotFoundException e) {
                showError("JDBC Driver Not Found", "MySQL JDBC driver not found in classpath");
                e.printStackTrace();
            } catch (SQLException e) {
                showError("Database Connection Failed", e.getMessage());
                e.printStackTrace();
            }
        }









    private void handleLogin(String username, String password) {
        String role = LoginService.login(username, password);
        if (role == null) {
            showError("Login Failed", "Invalid username or password.");
            return;
        }
// After successful login
        currentUsername = username;

        switch (role) {
            case "Admin":
                showAdminPage();
                break;
            case "HR Manager":
                showHrManagerPage();
                break;
            case "Employee":
                showEmployeePage();
                break;
            default:
                showError("Login Failed", "Unrecognized role.");
        }
    }


    // Hover effect helper
    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #ce93d8; -fx-text-fill: white; -fx-background-radius: 10;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #6a1b9a;"
        ));
    }



    private Node createReportGenerationView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Title
        Label title = new Label("Report Generation");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");

        // Report type selection
        ComboBox<String> reportTypeCombo = new ComboBox<>();
        reportTypeCombo.getItems().addAll(

                "Employee List",
                "Department Summary",
                "Attendance Summary"
        );
        reportTypeCombo.setPromptText("Select Report Type");

        // Date range for reports that need it
        HBox dateRangeBox = new HBox(10);
        DatePicker startDate = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker endDate = new DatePicker(LocalDate.now());
        dateRangeBox.getChildren().addAll(
                new Label("From:"), startDate,
                new Label("To:"), endDate
        );
        dateRangeBox.setVisible(false); // Initially hidden

        // Department filter (optional)
        ComboBox<String> departmentCombo = new ComboBox<>();
        departmentCombo.setPromptText("All Departments");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT department_name FROM departments");
            while (rs.next()) {
                departmentCombo.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load departments");
        }

        // Generate button
        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white;");


        // Report display area
        TableView<Map<String, String>> reportTable = new TableView<>();
        reportTable.setStyle("-fx-font-size: 14px;");

        // Show/hide date range based on report type
        reportTypeCombo.setOnAction(e -> {
            String selected = reportTypeCombo.getValue();
            dateRangeBox.setVisible(selected != null &&
                    (selected.equals("Attendance Summary")));
        });

        // Generate report action
        generateBtn.setOnAction(e -> {
            String reportType = reportTypeCombo.getValue();
            if (reportType == null) {
                showError("Selection Error", "Please select a report type");
                return;
            }

            try {
                ObservableList<Map<String, String>> reportData = FXCollections.observableArrayList();

                switch (reportType) {


                    case "Employee List":
                        reportData.addAll(generateEmployeeListReport(
                                departmentCombo.getValue()
                        ));
                        setupEmployeeListColumns(reportTable);
                        break;

                    case "Department Summary":
                        reportData.addAll(generateDepartmentSummaryReport());
                        setupDepartmentSummaryColumns(reportTable);
                        break;

                    case "Attendance Summary":
                        reportData.addAll(generateAttendanceSummaryReport(
                                startDate.getValue(),
                                endDate.getValue(),
                                departmentCombo.getValue()
                        ));
                        setupAttendanceSummaryColumns(reportTable);
                        break;
                }

                reportTable.setItems(reportData);

            } catch (SQLException ex) {
                showError("Report Generation Failed", ex.getMessage());
            }
        });

        // Export button
        Button exportBtn = new Button("Export to PDF");
        exportBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        exportBtn.setOnAction(e -> exportReportToPDF(reportTable));

        Button exportExcelBtn = new Button("Export to Excel");
        exportExcelBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        exportExcelBtn.setOnAction(e -> exportReportToExcel(reportTable));

        HBox buttonBox = new HBox(10, generateBtn, exportBtn, exportExcelBtn);


        container.getChildren().addAll(
                title,
                new Label("Report Type:"), reportTypeCombo,
                dateRangeBox,
                new Label("Filter by Department (optional):"), departmentCombo,
                buttonBox,
                reportTable
        );

        return container;
    }


    private List<Map<String, String>> generateEmployeeListReport(String departmentFilter) throws SQLException {
        List<Map<String, String>> reportData = new ArrayList<>();

        String sql = "SELECT id, name, department, position, phone_number, join_date " +
                "FROM employees WHERE is_active = 1 " +
                (departmentFilter != null && !departmentFilter.isEmpty() ? "AND department = ?" : "") +
                " ORDER BY name";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (departmentFilter != null && !departmentFilter.isEmpty()) {
                stmt.setString(1, departmentFilter);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("ID", rs.getString("id"));
                row.put("Name", rs.getString("name"));
                row.put("Department", rs.getString("department"));
                row.put("Position", rs.getString("position"));
                row.put("Phone", rs.getString("phone_number"));
                row.put("Join Date", rs.getString("join_date"));
                reportData.add(row);
            }
        }
        return reportData;
    }

    private void setupEmployeeListColumns(TableView<Map<String, String>> table) {
        table.getColumns().clear();

        String[] columns = {"ID", "Name", "Department", "Position", "Phone", "Join Date"};
        for (String col : columns) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(col);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(col)));
            table.getColumns().add(column);
        }
    }private List<Map<String, String>> generateDepartmentSummaryReport() throws SQLException {
        List<Map<String, String>> reportData = new ArrayList<>();

        String sql = "SELECT d.department_name, COUNT(e.id) as employee_count, " +
                "AVG(e.salary) as avg_salary, " +
                "MAX(e.salary) as max_salary, " +
                "MIN(e.salary) as min_salary " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.department_name = e.department AND e.is_active = 1 " +
                "GROUP BY d.department_name";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("Department", rs.getString("department_name"));
                row.put("Employee Count", String.valueOf(rs.getInt("employee_count")));
                row.put("Avg Salary", String.format("%.2f", rs.getDouble("avg_salary")));
                row.put("Max Salary", String.valueOf(rs.getDouble("max_salary")));
                row.put("Min Salary", String.valueOf(rs.getDouble("min_salary")));
                reportData.add(row);
            }
        }
        return reportData;
    }
    private void setupDepartmentSummaryColumns(TableView<Map<String, String>> table) {
        table.getColumns().clear();

        String[] columns = {"Department", "Employee Count", "Avg Salary", "Max Salary", "Min Salary"};
        for (String col : columns) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(col);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(col)));
            table.getColumns().add(column);
        }
    }

    private void exportReportToExcel(TableView<Map<String, String>> table) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(mainStage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Employee Report");

                // Create header row
                Row headerRow = sheet.createRow(0);
                int colNum = 0;
                for (TableColumn<Map<String, String>, ?> column : table.getColumns()) {
                    Cell cell = headerRow.createCell(colNum++);
                    cell.setCellValue(column.getText());
                }

                // Add data rows
                int rowNum = 1;
                for (Map<String, String> row : table.getItems()) {
                    Row dataRow = sheet.createRow(rowNum++);
                    colNum = 0;
                    for (TableColumn<Map<String, String>, ?> column : table.getColumns()) {
                        Cell cell = dataRow.createCell(colNum++);
                        String value = row.get(column.getText());
                        cell.setCellValue(value != null ? value : "");
                    }
                }

                // Auto-size columns
                for (int i = 0; i < table.getColumns().size(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write to file
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }

                showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                        "Report exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                showError("Export Failed", "Error creating Excel: " + e.getMessage());
            }
        }
    }

    private List<Map<String, String>> generateAttendanceSummaryReport(LocalDate startDate, LocalDate endDate, String departmentFilter) throws SQLException {
        List<Map<String, String>> reportData = new ArrayList<>();

        String sql = "SELECT e.id, e.name, e.department, " +
                "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) as present_days, " +
                "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) as absent_days, " +
                "SUM(CASE WHEN a.status = 'Leave' THEN 1 ELSE 0 END) as leave_days " +
                "FROM employees e " +
                "LEFT JOIN attendance a ON e.id = a.employee_id " +
                "AND a.attendance_date BETWEEN ? AND ? " +
                "WHERE e.is_active = 1 " +
                (departmentFilter != null ? "AND e.department = ? " : "") +
                "GROUP BY e.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            if (departmentFilter != null) {
                stmt.setString(3, departmentFilter);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("ID", rs.getString("id"));
                row.put("Name", rs.getString("name"));
                row.put("Department", rs.getString("department"));
                row.put("Present Days", rs.getString("present_days"));
                row.put("Absent Days", rs.getString("absent_days"));
                row.put("Leave Days", rs.getString("leave_days"));
                reportData.add(row);
            }
        }

        return reportData;
    }

    private void setupAttendanceSummaryColumns(TableView<Map<String, String>> table) {
        table.getColumns().clear();

        String[] columns = {"ID", "Name", "Department", "Present Days", "Absent Days", "Leave Days"};
        for (String col : columns) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(col);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(col)));
            table.getColumns().add(column);
        }
    }
    private void exportReportToPDF(TableView<Map<String, String>> table) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(mainStage);

        if (file != null) {
            try {
                // Create PDF document
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Employee Report");
                contentStream.endText();

                // Add table data
                float yPosition = 650;
                float margin = 100;
                float rowHeight = 20;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float colWidth = tableWidth / table.getColumns().size();

                // Draw headers
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                float xPosition = margin;
                for (TableColumn<Map<String, String>, ?> column : table.getColumns()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xPosition, yPosition);
                    contentStream.showText(column.getText());
                    contentStream.endText();
                    xPosition += colWidth;
                }
                yPosition -= rowHeight;

                // Draw rows
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                for (Map<String, String> row : table.getItems()) {
                    xPosition = margin;
                    for (TableColumn<Map<String, String>, ?> column : table.getColumns()) {
                        String value = row.get(column.getText());
                        if (value != null) {
                            contentStream.beginText();
                            contentStream.newLineAtOffset(xPosition, yPosition);
                            contentStream.showText(value);
                            contentStream.endText();
                        }
                        xPosition += colWidth;
                    }
                    yPosition -= rowHeight;

                    // Add new page if needed
                    if (yPosition < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = 700;
                    }
                }

                contentStream.close();
                document.save(file);
                document.close();

                showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                        "Report exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                showError("Export Failed", "Error creating PDF: " + e.getMessage());
            }
        }
    }



    private void showAdminPage() {
        // Main layout with left navigation and content area
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        StackPane contentArea = new StackPane();
        root.setCenter(contentArea);

        // Top header with gradient and icon
        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: linear-gradient(to right, #6a1b9a, #9c27b0);");

        // Add a cute icon
        Label icon = new Label("👑");
        icon.setStyle("-fx-font-size: 24px;");

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        header.getChildren().addAll(icon, title);
        root.setTop(header);

        // Left navigation with enhanced styling
        VBox navContainer = new VBox(15);
        navContainer.setPadding(new Insets(20));
        navContainer.setStyle("-fx-background-color: #f3e5f5; -fx-border-color: #ce93d8; -fx-border-width: 0 1 0 0;");
        navContainer.setPrefWidth(220);

        // Employee section
        Label empLabel = new Label("👥 Employee Management");
        empLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a; -fx-font-size: 16px;");

        VBox empOptions = new VBox(5);
        createNavButton("🔍 View Employees", empOptions);
        createNavButton("➕ Add Employee", empOptions);


        createNavButton("🔍 Search Employees", empOptions);

        createNavButton("📋 Generate Leave", empOptions, () -> {
            contentArea.getChildren().setAll(createGenerateLeaveView());
        });
        createNavButton("🔄 Manage Inactive", empOptions, contentArea, createInactiveEmployeeView());
        createNavButton("📊 Generate Attendance", empOptions, () -> {
            contentArea.getChildren().setAll(createAttendanceSummaryView());
        });
        createNavButton("📝 Register Leave", empOptions, () -> {
            contentArea.getChildren().setAll(createRegisterLeaveView());
        });



        // In the employee section of admin page
        createNavButton("📊 Generate Reports", empOptions, () -> {
            contentArea.getChildren().setAll(createReportGenerationView());
        });

        createNavButton("⏱️ Punch In", empOptions, () -> {
            contentArea.getChildren().setAll(createPunchInView());
        });

        // Department section
        Label deptLabel = new Label("🏢 Department Management");
        deptLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a; -fx-font-size: 16px;");

        VBox deptOptions = new VBox(5);
        createNavButton("👀 View Departments", deptOptions);
        createNavButton("✨ Add Department", deptOptions);
        createNavButton("🗑️ Remove Department", deptOptions);
        createNavButton("🔍 Search Department", deptOptions);
        createNavButton("🖊️ Update Department", deptOptions);


// NEW: Change Password Section
        Label changePassLabel = new Label("🔒Change password ");
        changePassLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a; -fx-font-size: 16px;");
        VBox changePassOptions = new VBox(5);
        createNavButton("Change Password", changePassOptions);

        navContainer.getChildren().addAll(
                changePassLabel, changePassOptions,   // Change Password section
                empLabel, empOptions,                  // Employee Management section
                deptLabel, deptOptions                 // Department Management section
        );
        root.setLeft(navContainer);

        // Center content area with subtle shadow


        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        root.setCenter(contentArea);

        // Bottom status bar
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #e1bee7; -fx-border-color: #ce93d8; -fx-border-width: 1 0 0 0;");

        Label statusIcon = new Label("💡");
        Label statusText = new Label("Ready");
        statusText.setStyle("-fx-text-fill: #4a148c;");

        Button backButton = createBackButton();
        backButton.setGraphic(new Label("👈"));
        backButton.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-background-radius: 15;");

        statusBar.getChildren().addAll(statusIcon, statusText);
        HBox.setHgrow(statusText, Priority.ALWAYS);
        statusBar.getChildren().add(backButton);
        root.setBottom(statusBar);

        // Set up navigation actions using ONLY your existing methods
        setupNavActions(navContainer, contentArea);

        Scene scene = new Scene(root, 1100, 750);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private Map<String, String> getEmployeeProfileData(String employeeId) {
        Map<String, String> employeeData = new HashMap<>();
        String sql = "SELECT * FROM employees WHERE id = ? AND is_active = 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                employeeData.put("ID", rs.getString("id"));
                employeeData.put("Name", rs.getString("name"));
                employeeData.put("Department", rs.getString("department"));
                employeeData.put("Position", rs.getString("position"));
                employeeData.put("Phone", rs.getString("phone_number"));
                employeeData.put("Education", rs.getString("education"));
                employeeData.put("Gender", rs.getString("sex"));
                employeeData.put("Salary", rs.getString("salary"));
                employeeData.put("Join Date", rs.getString("join_date"));
                employeeData.put("DOB", rs.getString("date_of_birth"));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load employee data: " + e.getMessage());
        }
        return employeeData;
    }

    private TableView<Map<String, String>> createInactiveEmployeeView() {
        TableView<Map<String, String>> table = createEmployeeTable(); // Reuse your existing table setup
        table.setItems(getInactiveEmployees());

        // Add reactivate button column
        TableColumn<Map<String, String>, Void> actions = new TableColumn<>("Actions");
        actions.setCellFactory(param -> new TableCell<>() {
            private final Button reactivateBtn = new Button("Reactivate");
            {
                reactivateBtn.setOnAction(event -> {
                    Map<String, String> employee = getTableView().getItems().get(getIndex());
                    if (reactivateEmployee(employee.get("ID"))) {
                        table.setItems(getInactiveEmployees()); // Refresh view
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : reactivateBtn);
            }
        });

        table.getColumns().add(actions);
        return table;
    }private void setupEmployeeTable() {
        employeeTable.getColumns().clear();

        TableColumn<Map<String, String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("ID")));

        TableColumn<Map<String, String>, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Name")));

        TableColumn<Map<String, String>, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Department")));

        TableColumn<Map<String, String>, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Position")));

        TableColumn<Map<String, String>, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Phone")));

        employeeTable.getColumns().addAll(idCol, nameCol, deptCol, positionCol, phoneCol);
    }
    private void showHrManagerPage() {
        // Main layout with left navigation and content area
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top header with gradient and icon
        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: linear-gradient(to right, #6a1b9a, #9c27b0);");

        // Add a cute icon (changed from crown to briefcase for HR)
        Label icon = new Label("💼");
        icon.setStyle("-fx-font-size: 24px;");

        Label title = new Label("HR Manager Dashboard");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Setup columns
        setupEmployeeTable();

        // Fill table with initial data
        employeeTable.setItems(FXCollections.observableArrayList(getEmployeeData()));

        header.getChildren().addAll(icon, title);
        root.setTop(header);
        StackPane contentArea = new StackPane(); // or VBox if you're using VBox
        root.setCenter(contentArea);
        // Left navigation with enhanced styling
        VBox navContainer = new VBox(15);
        navContainer.setPadding(new Insets(20));
        navContainer.setStyle("-fx-background-color: #f3e5f5; -fx-border-color: #ce93d8; -fx-border-width: 0 1 0 0;");
        navContainer.setPrefWidth(220);

        // Employee section (exactly same as Admin)
        Label empLabel = new Label("👥 Employee Management");
        empLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a; -fx-font-size: 16px;");

        VBox empOptions = new VBox(5);
        createNavButton("🔍 View Employees", empOptions, () -> {
            contentArea.getChildren().setAll(createEmployeeMasterDetailView());
        });
        createNavButton("➕ Add Employee", empOptions);


        createNavButton("🔍 Search Employees", empOptions);

        createNavButton("📝 Register Leave", empOptions);
        createNavButton("📊 Generate Attendance", empOptions);
        createNavButton("📋 Generate Leave", empOptions);
        createNavButton("🔄 Manage Inactive", empOptions, contentArea, createInactiveEmployeeView());
        createNavButton("⏱️ Punch In", empOptions, () -> {
            contentArea.getChildren().setAll(createPunchInView());
        });
        Label changePassLabel = new Label("🔒 Change Password ");
        changePassLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a; -fx-font-size: 16px;");
        VBox changePassOptions = new VBox(5);
        createNavButton("Change Password", changePassOptions);
// In the employee section of admin page
        createNavButton("📊 Generate Reports", empOptions, () -> {
            contentArea.getChildren().setAll(createReportGenerationView());
        });
        navContainer.getChildren().addAll(
                changePassLabel, changePassOptions,   // Change Password section
                empLabel, empOptions               // Employee Management section

        );
        root.setLeft(navContainer);

        // Center content area with subtle shadow

        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        root.setCenter(contentArea);

        // Bottom status bar
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #e1bee7; -fx-border-color: #ce93d8; -fx-border-width: 1 0 0 0;");

        Label statusIcon = new Label("💡");
        Label statusText = new Label("Ready");
        statusText.setStyle("-fx-text-fill: #4a148c;");

        Button backButton = createBackButton();
        backButton.setGraphic(new Label("👈"));
        backButton.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-background-radius: 15;");

        statusBar.getChildren().addAll(statusIcon, statusText);
        HBox.setHgrow(statusText, Priority.ALWAYS);
        statusBar.getChildren().add(backButton);
        root.setBottom(statusBar);

        // Set up navigation actions using ONLY your existing methods
        setupNavActions(navContainer, contentArea);

        Scene scene = new Scene(root, 1100, 750);
        mainStage.setScene(scene);
        mainStage.show();
    }


    // Update your createNavButton method to accept Runnable
    private Button createNavButton(String text, VBox container, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; "
                + "-fx-text-fill: #6a1b9a; -fx-font-size: 14px; -fx-alignment: baseline-left;");
        button.setOnAction(e -> action.run());
        addHoverEffect(button);

        container.getChildren().add(button);
        return button;
    }

    // Overload for buttons without action (if needed)
    private Button createNavButton(String text, VBox container) {
        return createNavButton(text, container, () -> {});
    }

    private Button createNavButton(String text, VBox container, StackPane contentArea, Node content) {
        return createNavButton(text, container, () -> {
            contentArea.getChildren().setAll(content);
        });
    }

    private void setupNavActions(VBox navContainer, StackPane contentArea) {
        for (Node node : navContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox section = (VBox) node;
                for (Node item : section.getChildren()) {
                    if (item instanceof Button) {
                        Button button = (Button) item;
                        // Remove emojis for matching
                        String buttonText = button.getText().replaceAll("[^\\w\\s]", "").trim();

                        button.setOnAction(e -> {

                            switch (buttonText) {
                                case "Search Employees":  // NEW
                                    contentArea.getChildren().setAll(createEmployeeSearchView());
                                    break;
                                case "Filter Employees":
                                    handleFilterEmployees(); // Your existing method
                                    break;
                                case "View Employees":   // Existing
                                    contentArea.getChildren().setAll(createEmployeeMasterDetailView());
                                    break;
                                case "Punch In":
                                    contentArea.getChildren().setAll(createPunchInView());
                                    break;
                                // ... other existing cases
// In the employee section of admin page
                                case  "Generate Reports":
                                    contentArea.getChildren().setAll(createReportGenerationView());
                                    break;
                                case "Add Employee":
                                    handleAddEmployee();
                                    break;

                                case "Update Employee":
                                    contentArea.getChildren().setAll(createUpdateEmployeeForm());
                                    break;
                                case "Update Department":
                                    contentArea.getChildren().setAll(createUpdateDepartmentForm());
                                    // After successful update:
                                    refreshEmployeeTable();
                                    refreshDepartmentView();
                                    break;
                                case "Change Password":
                                    contentArea.getChildren().setAll(createChangePasswordForm());
                                    break;
                                case "Manage Inactive":
                                    contentArea.getChildren().setAll(createInactiveEmployeeView());
                                    break;
                                case "Generate Attendance":
                                    contentArea.getChildren().setAll(createAttendanceSummaryView());
                                    break;

                                case "Register Leave":
                                    contentArea.getChildren().setAll(createRegisterLeaveView());
                                    break;
                                case "Generate Leave":
                                    contentArea.getChildren().setAll(createGenerateLeaveView());
                                    break;
                                case "View Departments":
                                    contentArea.getChildren().setAll(createDepartmentTable());
                                    break;
                                case "Add Department":
                                    handleAddDepartment();
                                    break;
                                case "Remove Department":
                                    handleRemoveDepartment();
                                    break;
                                case "Search Department":
                                    // Replace the dialog code with this single line:
                                    contentArea.getChildren().setAll(createDepartmentSearchView());
                                    break;
                            }
                        });
                    }
                }
            }
        }
    }
    private boolean checkOldPassword(String oldPass, Label messageLabel) {
        try {
            String sql = "SELECT password FROM employees WHERE id = ? AND is_active = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPass = rs.getString("password");
                if (storedPass.equals(oldPass)) {
                    return true;
                } else {
                    messageLabel.setText("Old password does not match our records.");
                    return false;
                }
            } else {
                messageLabel.setText("User not found in database.");
                return false;
            }
        } catch (SQLException ex) {
            messageLabel.setText("Database error while verifying old password.");
            ex.printStackTrace();
            return false;
        }
    }

    private boolean updatePassword(String newPass, Label messageLabel) {
        try {
            String sql = "UPDATE employees SET password = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPass);
            stmt.setString(2, currentUsername);
            int updatedRows = stmt.executeUpdate();
            if (updatedRows > 0) {
                return true;
            } else {
                messageLabel.setText("Password update failed: user not found.");
                return false;
            }
        } catch (SQLException ex) {
            messageLabel.setText("Database error while updating password.");
            ex.printStackTrace();
            return false;
        }
    }

    private VBox createChangePasswordForm() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Change Password");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        PasswordField oldPassField = new PasswordField();
        oldPassField.setPromptText("Enter Old Password");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("Enter New Password (6 letters/numbers)");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm New Password");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        Button changeBtn = new Button("Change Password");
        changeBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-background-radius: 15;");
        changeBtn.setOnAction(e -> {
            String oldPass = oldPassField.getText();
            String newPass = newPassField.getText();
            String confirmPass = confirmPassField.getText();

            // Basic validations
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                messageLabel.setText("Please fill in all fields.");
                return;
            }

            if (!newPass.matches("[a-zA-Z0-9]{6}")) {
                messageLabel.setText("New password must be exactly 6 letters and/or numbers.");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                messageLabel.setText("New password and confirmation do not match.");
                return;
            }

            // Check old password correctness from DB (with detailed error)
            if (!checkOldPassword(oldPass, messageLabel)) {
                // messageLabel is set inside checkOldPassword on failure
                return;
            }

            // Update password in DB (with detailed error)
            if (updatePassword(newPass, messageLabel)) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Password changed successfully.");
                oldPassField.clear();
                newPassField.clear();
                confirmPassField.clear();
            }
            // If updatePassword returns false, messageLabel is set inside the method
        });

        layout.getChildren().addAll(title, oldPassField, newPassField, confirmPassField, changeBtn, messageLabel);
        return layout;
    }

    private void loadDepartmentDataOnly(ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        try (
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM departments")) {
            while (rs.next()) {
                comboBox.getItems().add(rs.getString("department_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean updateDepartmentInDB(String id, String newName) {
        String oldName = null;

        try {
            conn.setAutoCommit(false);

            // 1. Get old department name
            String getOldNameSql = "SELECT department_name FROM departments WHERE department_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getOldNameSql)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    oldName = rs.getString("department_name");
                }
            }

            // 2. Update department name
            String updateDeptSql = "UPDATE departments SET department_name = ? WHERE department_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDeptSql)) {
                stmt.setString(1, newName);
                stmt.setString(2, id);
                int rows = stmt.executeUpdate();

                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Update all employee records that reference this department
            if (oldName != null) {
                String updateEmployeesSql = "UPDATE employees SET department = ? WHERE department = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateEmployeesSql)) {
                    stmt.setString(1, newName);
                    stmt.setString(2, oldName);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            showError("Database Error", "Failed to update department: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    private VBox createUpdateEmployeeForm() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Title with icon
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("✏️");
        icon.setStyle("-fx-font-size: 24px;");
        Label title = new Label("Update Employee Information");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");
        titleBox.getChildren().addAll(icon, title);

        // Employee ID input
        HBox idBox = new HBox(10);
        Label idLabel = new Label("Employee ID:");
        idLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");
        TextField idField = new TextField();
        idField.setPromptText("Enter employee ID");
        idField.setStyle(textFieldStyle());
        Button searchBtn = new Button("Search");
        searchBtn.setStyle(loginButtonStyle());
        idBox.getChildren().addAll(idLabel, idField, searchBtn);

        // Fields to update (initially disabled)
        ComboBox<String> fieldCombo = new ComboBox<>();
        fieldCombo.getItems().addAll("Name", "Department", "Position", "Education", "Phone", "Salary");
        fieldCombo.setPromptText("Select field to update");
        fieldCombo.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
        fieldCombo.setDisable(true);

        // Current value display
        Label currentValueLabel = new Label("Current value: ");
        currentValueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a;");
        Label currentValue = new Label();
        currentValue.setStyle("-fx-font-style: italic;");

        // New value input
        VBox inputContainer = new VBox(10);
        inputContainer.setVisible(false);

        Label newValueLabel = new Label("New value:");
        newValueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a;");

        // We'll dynamically change this based on selection
        StackPane inputPane = new StackPane();
        TextField textInput = new TextField();
        textInput.setStyle(textFieldStyle());
        ComboBox<String> comboInput = new ComboBox<>();
        comboInput.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
        inputPane.getChildren().add(textInput);

        // Department combo for when department is selected
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM departments")) {
            while (rs.next()) {
                deptCombo.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load departments");
        }

        // Education combo
        ComboBox<String> educationCombo = new ComboBox<>();
        educationCombo.getItems().addAll("High School", "Bachelor's", "Master's", "PhD");
        educationCombo.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");

        // Position combo
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("Manager", "Developer","Security Guard","Assistant","Analyst", "Intern","Cleaner","");
        positionCombo.setStyle("-fx-background-radius: 15; -fx-font-size: 14px;");

        // Update button
        Button updateBtn = new Button("Update");
        updateBtn.setStyle(loginButtonStyle());
        updateBtn.setDisable(true);

        // Status message
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px;");

        // Layout
        inputContainer.getChildren().addAll(newValueLabel, inputPane);
        container.getChildren().addAll(titleBox, idBox, fieldCombo,
                currentValueLabel, currentValue, inputContainer, updateBtn, statusLabel);

        // Search employee action
        searchBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                showError("Input Error", "Please enter an employee ID");
                return;
            }

            Map<String, String> employee = findEmployeeById(id);
            if (employee != null) {
                fieldCombo.setDisable(false);
                statusLabel.setText("");
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Employee found! Select field to update.");

                // Store employee data for later use
                fieldCombo.setUserData(employee);
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Employee not found!");
                fieldCombo.setDisable(true);
                inputContainer.setVisible(false);
                updateBtn.setDisable(true);
            }
        });

        // Field selection action
        fieldCombo.setOnAction(e -> {
            Map<String, String> employee = (Map<String, String>) fieldCombo.getUserData();
            String selectedField = fieldCombo.getValue();

            if (selectedField == null || employee == null) return;

            // Show current value
            currentValue.setText(employee.get(selectedField.toLowerCase()));

            // Configure input based on field type
            inputPane.getChildren().clear();

            switch (selectedField) {
                case "Name":
                    textInput.setPromptText("Enter new name (10-20 chars)");
                    textInput.setText(employee.get("name"));
                    inputPane.getChildren().add(textInput);
                    break;

                case "Department":
                    deptCombo.setValue(employee.get("department"));
                    inputPane.getChildren().add(deptCombo);
                    break;

                case "Position":
                    positionCombo.setValue(employee.get("position"));
                    inputPane.getChildren().add(positionCombo);
                    break;

                case "Education":
                    educationCombo.setValue(employee.get("education"));
                    inputPane.getChildren().add(educationCombo);
                    break;

                case "Phone":
                    textInput.setPromptText("Enter new phone (09xxxxxxxx)");
                    textInput.setText(employee.get("phone_number"));
                    inputPane.getChildren().add(textInput);
                    break;

                case "Salary":
                    textInput.setPromptText("Enter new salary");
                    textInput.setText(employee.get("salary"));
                    inputPane.getChildren().add(textInput);
                    break;
            }

            inputContainer.setVisible(true);
            updateBtn.setDisable(false);
        });

        // Update action
        updateBtn.setOnAction(e -> {
            Map<String, String> employee = (Map<String, String>) fieldCombo.getUserData();
            String selectedField = fieldCombo.getValue();
            String newValue = "";
            String id = employee.get("id");

            // Get the new value based on input type
            if (inputPane.getChildren().get(0) instanceof TextField) {
                newValue = ((TextField)inputPane.getChildren().get(0)).getText();
            } else if (inputPane.getChildren().get(0) instanceof ComboBox) {
                newValue = ((ComboBox<String>)inputPane.getChildren().get(0)).getValue();
            }

            // Validate input
            if (newValue == null || newValue.trim().isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Please enter a valid value!");
                return;
            }

            // Field-specific validation
            switch (selectedField) {
                case "Name":
                    if (newValue.length() < 10 || newValue.length() > 20 || !newValue.contains(" ")) {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText("Name must be 10-20 chars with space");
                        return;
                    }
                    break;

                case "Phone":
                    if (!newValue.matches("09\\d{8}")) {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText("Phone must be 10 digits starting with 09");
                        return;
                    }
                    break;

                case "Salary":
                    if (!newValue.matches("\\d+")) {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText("Salary must be numeric");
                        return;
                    }
                    break;
            }

            // Build SQL update
            String columnName = "";
            switch (selectedField) {
                case "Name": columnName = "name"; break;
                case "Department": columnName = "department"; break;
                case "Position": columnName = "position"; break;
                case "Education": columnName = "education"; break;
                case "Phone": columnName = "phone_number"; break;
                case "Salary": columnName = "salary"; break;
            }

            String sql = "UPDATE employees SET " + columnName + " = ? WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Handle different data types
                if (selectedField.equals("Salary")) {
                    stmt.setBigDecimal(1, new BigDecimal(newValue));
                } else {
                    stmt.setString(1, newValue);
                }
                stmt.setString(2, id);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText(selectedField + " updated successfully!");

                    // If department changed, update department counts
                    if (selectedField.equals("Department")) {
                        String oldDept = employee.get("department");
                        updateDepartmentCount(oldDept, -1);
                        updateDepartmentCount(newValue, 1);
                    }

                    // Refresh the employee data
                    employee.put(columnName, newValue);
                    fieldCombo.setUserData(employee);
                    currentValue.setText(newValue);
                } else {
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Update failed!");
                }
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error updating record");
            }
        });

        return container;
    }
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private VBox createUpdateDepartmentForm() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Update Department");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField deptIdField = new TextField();
        deptIdField.setPromptText("Enter Department ID");

        TextField deptNameField = new TextField();
        deptNameField.setPromptText("New Department Name");

        Button updateBtn = new Button("Update Department");
        updateBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white;");

        updateBtn.setOnAction(e -> {
            String id = deptIdField.getText().trim();
            String name = deptNameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                showError("Input Error", "Please fill all fields.");
                return;
            }


            boolean success = updateDepartmentInDB(id, name);
            if (success) {
                showInfo("Success", "Department updated.");
                deptIdField.clear();
                deptNameField.clear();
            } else {
                showError("Error", "Department ID not found or update failed.");
            }
        });


        layout.getChildren().addAll(title, deptIdField, deptNameField, updateBtn);
        return layout;
    }private VBox createEmployeeSearchView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // TabPane for search types
        TabPane searchTypePane = new TabPane();
        Tab searchTab = new Tab("🔍 Basic Search");
        Tab filterTab = new Tab("🎚️ Advanced Filter");
        searchTypePane.getTabs().addAll(searchTab, filterTab);

        // Results Table (shared between both tabs)
        TableView<Map<String, String>> resultsTable = createEmployeeTable();
        resultsTable.setStyle("-fx-font-size: 14px;");

        /* ===== BASIC SEARCH TAB ===== */
        VBox searchContent = new VBox(10);
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter ID or Name...");
        searchField.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-background-radius: 15;");
        searchBtn.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Map<String, String>> results = basicEmployeeSearch(searchTerm);
                resultsTable.setItems(FXCollections.observableArrayList(results));
            } else {
                // Reset to all employees if search is empty
                resultsTable.setItems(FXCollections.observableArrayList(getEmployeeData()));
            }
        });

        searchBox.getChildren().addAll(searchField, searchBtn);
        searchContent.getChildren().addAll(searchBox);
        searchTab.setContent(searchContent);

        /* ===== ADVANCED FILTER TAB (with scroll) ===== */
        ScrollPane filterScroll = new ScrollPane();
        filterScroll.setFitToWidth(true);
        filterScroll.setStyle("-fx-background: white; -fx-border-color: transparent;");
        filterScroll.setPrefViewportHeight(250); // Fixed height for scrolling

        VBox filterContent = new VBox(15);
        filterContent.setPadding(new Insets(15));

        // Filter Grid
        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(15);
        filterGrid.setVgap(10);
        filterGrid.setPadding(new Insets(0, 15, 15, 0));

        // Department filter (load from DB)
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.setPromptText("All Departments");
        loadDepartmentData(deptCombo);

        // Position filter (predefined options)
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.setPromptText("All Positions");
        positionCombo.getItems().addAll("All Positions", "Manager", "Developer", "HR", "Analyst", "Intern");

        // Gender filter
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("All", "Male", "Female");
        genderCombo.setValue("All");

        // Seniority filter (years since joining)
        ComboBox<String> seniorityCombo = new ComboBox<>();
        seniorityCombo.getItems().addAll("All", ">1 Year", ">3 Years", ">5 Years");
        seniorityCombo.setValue("All");

        // Salary range filter
        ComboBox<String> salaryCombo = new ComboBox<>();
        salaryCombo.getItems().addAll("All", "<10,000", "10,000-30,000", "30,000-50,000", ">50,000");
        salaryCombo.setValue("All");

        // Add filters to grid
        filterGrid.add(new Label("Department:"), 0, 0);
        filterGrid.add(deptCombo, 1, 0);
        filterGrid.add(new Label("Position:"), 0, 1);
        filterGrid.add(positionCombo, 1, 1);
        filterGrid.add(new Label("Gender:"), 0, 2);
        filterGrid.add(genderCombo, 1, 2);
        filterGrid.add(new Label("Seniority:"), 0, 3);
        filterGrid.add(seniorityCombo, 1, 3);
        filterGrid.add(new Label("Salary:"), 0, 4);
        filterGrid.add(salaryCombo, 1, 4);

        // Apply Filter Button (visible in scroll)
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button applyFilterBtn = new Button("Apply Filters");
        applyFilterBtn.setStyle("-fx-background-color: #7b1fa2; -fx-text-fill: white; -fx-font-weight: bold;");
        applyFilterBtn.setMinWidth(120);
        applyFilterBtn.setMinHeight(35);

        buttonBox.getChildren().add(applyFilterBtn);

        // Add components to filter content
        filterContent.getChildren().addAll(filterGrid, buttonBox);
        filterScroll.setContent(filterContent);
        filterTab.setContent(filterScroll);

        /* ===== FIXED FILTER LOGIC (similar to report generation) ===== */
        applyFilterBtn.setOnAction(e -> {
            try {
                // Build SQL query dynamically based on selected filters
                StringBuilder sql = new StringBuilder(
                        "SELECT id, name, department, position, phone_number, join_date, salary " +
                                "FROM employees WHERE is_active = 1 "
                );

                List<String> conditions = new ArrayList<>();
                List<Object> params = new ArrayList<>();

                // Department filter
                if (!deptCombo.getValue().equals("All Departments")) {
                    conditions.add("department = ?");
                    params.add(deptCombo.getValue());
                }

                // Position filter
                if (!positionCombo.getValue().equals("All Positions")) {
                    conditions.add("position = ?");
                    params.add(positionCombo.getValue());
                }

                // Gender filter
                if (!genderCombo.getValue().equals("All")) {
                    conditions.add("sex = ?");
                    params.add(genderCombo.getValue());
                }

                // Seniority filter (based on join date)
                if (!seniorityCombo.getValue().equals("All")) {
                    LocalDate cutoffDate = LocalDate.now();
                    switch (seniorityCombo.getValue()) {
                        case ">1 Year": cutoffDate = cutoffDate.minusYears(1); break;
                        case ">3 Years": cutoffDate = cutoffDate.minusYears(3); break;
                        case ">5 Years": cutoffDate = cutoffDate.minusYears(5); break;
                    }
                    conditions.add("join_date <= ?");
                    params.add(Date.valueOf(cutoffDate));
                }

                // Salary range filter
                if (!salaryCombo.getValue().equals("All")) {
                    switch (salaryCombo.getValue()) {
                        case "<10,000": conditions.add("salary < 10000"); break;
                        case "10,000-30,000": conditions.add("salary BETWEEN 10000 AND 30000"); break;
                        case "30,000-50,000": conditions.add("salary BETWEEN 30000 AND 50000"); break;
                        case ">50,000": conditions.add("salary > 50000"); break;
                    }
                }

                // Combine conditions
                if (!conditions.isEmpty()) {
                    sql.append(" AND ").append(String.join(" AND ", conditions));
                }

                // Execute query
                PreparedStatement stmt = conn.prepareStatement(sql.toString());
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = stmt.executeQuery();
                List<Map<String, String>> filteredEmployees = new ArrayList<>();

                while (rs.next()) {
                    Map<String, String> employee = new HashMap<>();
                    employee.put("ID", rs.getString("id"));
                    employee.put("Name", rs.getString("name"));
                    employee.put("Department", rs.getString("department"));
                    employee.put("Position", rs.getString("position"));
                    employee.put("Phone", rs.getString("phone_number"));
                    employee.put("Join Date", rs.getString("join_date"));
                    employee.put("Salary", rs.getString("salary"));
                    filteredEmployees.add(employee);
                }

                // Update table with filtered results
                resultsTable.setItems(FXCollections.observableArrayList(filteredEmployees));

            } catch (SQLException ex) {
                showError("Database Error", "Failed to filter employees: " + ex.getMessage());
            }
        });

        // Add all components to main container
        container.getChildren().addAll(
                new Label("Employee Search"),
                searchTypePane,
                resultsTable
        );

        return container;
    }

    // Helper method to create compact combo boxes
    private ComboBox<String> createCompactComboBox(String prompt) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(prompt);
        combo.setMaxWidth(150);
        combo.setStyle("-fx-font-size: 12px; -fx-padding: 2 5;");
        return combo;
    }
    // Add this new method for basic search
    private List<Map<String, String>> basicEmployeeSearch(String searchTerm) {
        List<Map<String, String>> results = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE is_active = 1 AND " +
                "(id LIKE ? OR name LIKE ?) ORDER BY name";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> employee = new HashMap<>();
                employee.put("ID", rs.getString("id"));
                employee.put("Name", rs.getString("name"));
                employee.put("Department", rs.getString("department"));
                employee.put("Position", rs.getString("position"));
                employee.put("Gender", rs.getString("sex"));
                employee.put("Join Date", rs.getString("join_date"));
                employee.put("Salary", rs.getString("salary"));
                results.add(employee);
            }
        } catch (SQLException e) {
            showError("Search Failed", e.getMessage());
        }
        return results;
    }

    private VBox createDepartmentSearchView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Search Box
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter department name...");
        searchField.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-background-radius: 15;");
        searchBtn.setOnAction(e -> handleDepartmentSearch(searchField.getText()));

        searchBox.getChildren().addAll(searchField, searchBtn);

        // Results Table
        TableView<Map<String, String>> resultsTable = createDepartmentTable();
        resultsTable.setItems(FXCollections.observableArrayList(getDepartmentData()));

        container.getChildren().addAll(
                new Label("Search Departments"),
                searchBox,
                resultsTable
        );
        return container;
    }
   private TableView<Map<String, String>> createEmployeeTable() {
        TableView<Map<String, String>> table = new TableView<>();

        // Define only the columns we want to show
        String[] columnsToShow = {"ID", "Name", "Department", "Position", "Phone"};

        for (String col : columnsToShow) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(col);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(col)));
            table.getColumns().add(column);
        }

        // Style the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 14px;");

        // Load data
        table.setItems(FXCollections.observableArrayList(getEmployeeData()));

        return table;
    }
    private void showEmployeeDetailDialog(Map<String, String> employee) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee - " + employee.get("Name"));

        // Create form with tabs
        TabPane tabPane = new TabPane();

        // Basic Info Tab
        Tab basicTab = new Tab("Basic Info");
        basicTab.setClosable(false);
        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(10);
        basicGrid.setVgap(10);
        basicGrid.setPadding(new Insets(15));

        // ID (read-only)
        TextField idField = new TextField(employee.get("ID"));
        idField.setDisable(true);

        // Name Field with validation and default value
        TextField nameField = new TextField(employee.get("Name"));
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 20 || !newVal.matches("^[A-Za-z ]*$")) {
                nameField.setText(oldVal);
            }
        });

        // Department ComboBox with default value
        ComboBox<String> deptCombo = new ComboBox<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM departments")) {
            while (rs.next()) {
                deptCombo.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load departments");
        }
        deptCombo.setValue(employee.get("Department"));

        // Phone Field with proper validation and default value
        TextField phoneField = new TextField(employee.get("Phone"));
        // This listener allows proper editing while enforcing format
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("09\\d{0,8}")) {
                if (newVal.isEmpty()) {
                    phoneField.setText("");
                } else {
                    phoneField.setText(oldVal);
                }
            } else if (newVal.length() > 10) {
                phoneField.setText(oldVal);
            }
        });

        // Add fields to basic grid
        int row = 0;
        addFormRow(basicGrid, "ID:", idField, row++);
        addFormRow(basicGrid, "Name* (10-20 chars):", nameField, row++);
        addFormRow(basicGrid, "Department*:", deptCombo, row++);
        addFormRow(basicGrid, "Phone* (09xxxxxxxx):", phoneField, row++);

        basicTab.setContent(basicGrid);

        // Employment Tab
        Tab employmentTab = new Tab("Employment");
        employmentTab.setClosable(false);
        GridPane employmentGrid = new GridPane();
        employmentGrid.setHgap(10);
        employmentGrid.setVgap(10);
        employmentGrid.setPadding(new Insets(15));

        // Position ComboBox with default value
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("Manager", "Developer", "HR", "Analyst", "Intern");
        positionCombo.setValue(employee.get("Position"));

        // Education ComboBox with default value
        ComboBox<String> educationCombo = new ComboBox<>();
        educationCombo.getItems().addAll("High School", "Bachelor's", "Master's", "PhD");
        educationCombo.setValue(employee.get("Education"));

        // Salary Field with default value
        TextField salaryField = new TextField(employee.get("Salary"));
        salaryField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                salaryField.setText(oldVal);
            }
        });

        // Add fields to employment grid
        row = 0;
        addFormRow(employmentGrid, "Position*:", positionCombo, row++);
        addFormRow(employmentGrid, "Education*:", educationCombo, row++);
        addFormRow(employmentGrid, "Salary*:", salaryField, row++);

        employmentTab.setContent(employmentGrid);

        tabPane.getTabs().addAll(basicTab, employmentTab);

        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Set the result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Validate inputs
                if (nameField.getText().length() < 10 || !nameField.getText().contains(" ")) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Name", "Name must be 10-20 characters with space");
                    return null;
                }
                if (phoneField.getText().length() != 10) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Phone", "Phone must be 10 digits starting with 09");
                    return null;
                }

                Map<String, String> updated = new HashMap<>(employee);
                updated.put("name", nameField.getText());
                updated.put("department", deptCombo.getValue());
                updated.put("phone_number", phoneField.getText());
                updated.put("position", positionCombo.getValue());
                updated.put("education", educationCombo.getValue());
                updated.put("salary", salaryField.getText());
                return updated;
            }
            return null;
        });

        // Set dialog size
        dialog.getDialogPane().setPrefSize(600, 400);

        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(updatedEmployee -> {
            if (updateEmployeeInDatabase(updatedEmployee)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully!");
                refreshEmployeeTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee.");
            }
        });
    }

    private Node createEmployeeMasterDetailView() {
        SplitPane splitPane = new SplitPane();

        TableView<Map<String, String>> masterTable = createEmployeeTable();
        ObservableList<Map<String, String>> allEmployees = FXCollections.observableArrayList(getEmployeeData());
        allEmployees.sort(Comparator.comparing(m -> m.get("Name"), String.CASE_INSENSITIVE_ORDER));

        // Create scrollable container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Create container with pagination on top
        VBox tableContainer = new VBox(5);
        tableContainer.setPadding(new Insets(10));

        // Create pagination controls with scroll awareness
        HBox paginationControls = createPaginationControls(masterTable, allEmployees);
        paginationControls.setAlignment(Pos.CENTER);

        // Add components to container
        tableContainer.getChildren().addAll(
                paginationControls,
                masterTable
        );

        scrollPane.setContent(tableContainer);

        // Detail side (initially empty)
        StackPane detailPane = new StackPane();
        detailPane.setPadding(new Insets(10));

        // When row is selected, show details
        masterTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                detailPane.getChildren().setAll(createEmployeeDetailView(newVal));
            }
        });

        splitPane.getItems().addAll(scrollPane, detailPane);
        return splitPane;
    }
    private Node createEmployeeDetailView(Map<String, String> employee) {
        VBox detailBox = new VBox(15);
        detailBox.setPadding(new Insets(20));

        // Display employee details
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);

        int row = 0;
        for (Map.Entry<String, String> entry : employee.entrySet()) {
            infoGrid.add(new Label(entry.getKey() + ":"), 0, row);
            infoGrid.add(new Label(entry.getValue()), 1, row);
            row++;
        }

        // Action buttons
        HBox buttonBox = new HBox(10);
        Button editBtn = new Button("Edit");
        Button removeBtn = new Button("Remove");

        editBtn.setOnAction(e -> showEmployeeDetailDialog(employee));
        removeBtn.setOnAction(e -> handleRemoveEmployee(employee.get("ID")));

        buttonBox.getChildren().addAll(editBtn, removeBtn);

        detailBox.getChildren().addAll(
                new Label("Employee Details"),
                infoGrid,
                buttonBox
        );

        return detailBox;
    }


    // Refresh the employee table view from DB
    private void refreshEmployeeTable() {
        // Refresh the employee table
        employeeTable.setItems(FXCollections.observableArrayList(getEmployeeData()));

        // If you have a separate employee list view, refresh it too

    }
    private boolean updateEmployeeInDatabase(Map<String, String> employee) {
        String oldDepartment = null;
        String newDepartment = employee.get("department");
        String employeeId = employee.get("ID");

        try {
            conn.setAutoCommit(false);

            // 1. Get old department for count adjustment
            String getDeptSql = "SELECT department FROM employees WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getDeptSql)) {
                stmt.setString(1, employeeId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    oldDepartment = rs.getString("department");
                }
            }

            // 2. Update employee record
            String updateSql = "UPDATE employees SET " +
                    "name = ?, department = ?, position = ?, " +
                    "education = ?, phone_number = ?, salary = ? " +
                    "WHERE id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, employee.get("name"));
                stmt.setString(2, newDepartment);
                stmt.setString(3, employee.get("position"));
                stmt.setString(4, employee.get("education"));
                stmt.setString(5, employee.get("phone_number"));
                stmt.setBigDecimal(6, new BigDecimal(employee.get("salary")));
                stmt.setString(7, employeeId);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // 3. Update department counts if department changed
                    if (oldDepartment != null && !oldDepartment.equals(newDepartment)) {
                        // Decrement old department
                        updateDepartmentCount(oldDepartment, -1);
                        // Increment new department
                        updateDepartmentCount(newDepartment, 1);
                    }

                    conn.commit();
                    refreshEmployeeTable();
                    return true;
                }
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            showAlert(Alert.AlertType.ERROR, "Update Failed", e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    private ObservableList<Map<String, String>> getEmployeeData() {
        ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employees WHERE is_active = 1 ORDER BY name ASC"; // Added ORDER BY

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("ID", rs.getString("id"));
                row.put("Name", rs.getString("name"));
                row.put("Department", rs.getString("department"));
                row.put("Position", rs.getString("position"));
                row.put("Phone", rs.getString("phone_number"));
                row.put("Education", rs.getString("education"));
                row.put("Gender", rs.getString("sex"));
                row.put("Salary", rs.getString("salary"));
                row.put("JoinDate", rs.getString("join_date"));
                row.put("DOB", rs.getString("date_of_birth"));
                row.put("Password", rs.getString("password")); // Added password
                data.add(row);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
        return data;
    }private TableView<Map<String, String>> createDepartmentTable() {
        TableView<Map<String, String>> table = new TableView<>();

        // Styling
        table.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f8f8;");
        table.setPlaceholder(new Label("No departments found"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID Column
        TableColumn<Map<String, String>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("ID")));
        idCol.setStyle("-fx-alignment: CENTER;");
        idCol.setComparator(String::compareToIgnoreCase); // Case-insensitive sorting

        // Name Column
        TableColumn<Map<String, String>, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Name")));
        nameCol.setStyle("-fx-alignment: CENTER_LEFT;");
        nameCol.setComparator(String::compareToIgnoreCase); // Case-insensitive sorting
        nameCol.setSortType(TableColumn.SortType.ASCENDING); // Default sort

        // Employee Count Column
        TableColumn<Map<String, String>, String> empCountCol = new TableColumn<>("Employees");
        empCountCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Employees")));
        empCountCol.setStyle("-fx-alignment: CENTER;");
        // Numeric sorting for employee count
        empCountCol.setComparator((s1, s2) -> {
            try {
                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        });

        // Add columns to table
        table.getColumns().addAll(idCol, nameCol, empCountCol);

        // Set sorting behavior
        table.setSortPolicy(t -> {
            if (t.getSortOrder().isEmpty()) {
                return true; // No sorting needed
            }

            Comparator<Map<String, String>> comparator = (r1, r2) -> {
                String colName = t.getSortOrder().get(0).getText();
                String val1 = r1.get(colName);
                String val2 = r2.get(colName);

                // Handle null values
                if (val1 == null) return val2 == null ? 0 : -1;
                if (val2 == null) return 1;

                return val1.compareToIgnoreCase(val2);
            };

            // Apply sorting based on sort type
            FXCollections.sort(t.getItems(),
                    t.getSortOrder().get(0).getSortType() == TableColumn.SortType.ASCENDING
                            ? comparator
                            : comparator.reversed());

            return true;
        });

        // Set default sort order
        table.getSortOrder().add(nameCol);


        // Load data
        table.setItems(FXCollections.observableArrayList(getDepartmentData()));

        return table;
    }
    private ObservableList<Map<String, String>> getDepartmentData() {
        ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
        // Added employee_count to the query while keeping all original columns
        String sql = "SELECT department_id as ID, department_name as Name, employee_count as Employees FROM departments";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                // Preserve all original mappings
                row.put("ID", rs.getString("ID"));
                row.put("Name", rs.getString("Name"));
                // Add the new employee count
                row.put("Employees", rs.getString("Employees"));
                data.add(row);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }

        return data;
    }


    private void handleAddDepartment() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("✨ Add Department");
        dialog.setHeaderText("Enter New Department Name:");

        // Style to match employee dialogs
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #f3e5f5;" +
                        "-fx-border-color: #ce93d8;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 5;"
        );
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-weight: bold;"
        );
        dialogPane.lookup(".label").setStyle(
                "-fx-text-fill: #6a1b9a;" +
                        "-fx-font-size: 14px;"
        );

        // Style the input field
        TextField editor = dialog.getEditor();
        editor.setStyle(
                "-fx-background-radius: 15;" +
                        "-fx-padding: 5 10;" +
                        "-fx-border-color: #ba68c8;"
        );

        dialog.showAndWait().ifPresent(departmentName -> {
            if (departmentName == null || departmentName.trim().isEmpty()) {
                showError("Invalid Input", "Department name cannot be empty");
                return;
            }

            // Check if department already exists
            if (departmentExists(departmentName)) {
                showError("Duplicate Department", "A department with this name already exists");
                return;
            }

            // Attempt to add department
            if (addDepartment(departmentName)) {
                // Show success message with consistent styling
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Department Added");
                successAlert.setContentText("The department '" + departmentName + "' was successfully added.");

                // Style the alert to match
                DialogPane successPane = successAlert.getDialogPane();
                successPane.setStyle("-fx-background-color: linear-gradient(to bottom, #e0eafc, #cfdef3);");
                successPane.lookup(".header-panel").setStyle("-fx-background-color: transparent;");
                successPane.lookup(".label").setStyle("-fx-font-size: 16px; -fx-font-family: 'Verdana';");

                successAlert.showAndWait();

                // Refresh department view if needed
                refreshDepartmentView();
            } else {
                showError("Add Failed", "Failed to add department '" + departmentName + "'");
            }
        });
    }
    private boolean departmentExists(String departmentName) {
        // Validate input
        if (departmentName == null || departmentName.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM departments WHERE department_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departmentName.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Returns true if count > 0
                }
            }
        } catch (SQLException e) {
            // Log the error but don't show to user to avoid duplicate messages
            System.err.println("Error checking department existence: " + e.getMessage());
        }

        return false;
    }
    private void refreshDepartmentView() {
        // Get the current content area from the main stage
        BorderPane root = (BorderPane) mainStage.getScene().getRoot();
        StackPane contentArea = (StackPane) root.getCenter();

        // Find the department table in the current view
        for (Node node : contentArea.getChildren()) {
            if (node instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) node;
                if (scrollPane.getContent() instanceof TableView) {
                    TableView<Map<String, String>> tableView = (TableView<Map<String, String>>) scrollPane.getContent();

                    // Check if this is the department table by looking at columns
                    if (tableView.getColumns().stream().anyMatch(col -> col.getText().equals("Department"))) {
                        // Refresh the table data
                        tableView.setItems(FXCollections.observableArrayList(getDepartmentData()));
                        tableView.refresh();
                        return;
                    }
                }
            } else if (node instanceof TableView) {
                TableView<Map<String, String>> tableView = (TableView<Map<String, String>>) node;

                // Check if this is the department table by looking at columns
                if (tableView.getColumns().stream().anyMatch(col -> col.getText().equals("Department"))) {
                    // Refresh the table data
                    tableView.setItems(FXCollections.observableArrayList(getDepartmentData()));
                    tableView.refresh();
                    return;
                }
            }
        }

        // If we didn't find the table, log a warning
        System.out.println("Warning: Could not find department table to refresh");
    }private boolean addDepartment(String departmentName) {
        // First check if department already exists
        if (departmentExists(departmentName)) {
            showError("Duplicate Department", departmentName + " already exists");
            return false;
        }

        String sql = "INSERT INTO departments (department_name, employee_count) VALUES (?, 0)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, departmentName.trim());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update all department dropdowns in the UI
                refreshAllDepartmentDropdowns();

                // Get the generated ID if needed
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        // Can use newId for future reference
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            showError("Add Department Failed", e.getMessage());
        }
        return false;
    }

    private void refreshAllDepartmentDropdowns() {
        // This would need to iterate through all your department comboboxes
        // and reload their data. Implementation depends on your UI structure.
        // Example:
        // departmentCombo1.getItems().setAll(getAllDepartmentNames());
        // departmentCombo2.getItems().setAll(getAllDepartmentNames());
        // etc.
    }
    private void handleDepartmentSearch(String searchTerm) {
        String sql = "SELECT department_id as ID, department_name as Name, employee_count as Employees " +
                "FROM departments WHERE department_name LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("ID", rs.getString("ID"));
                row.put("Name", rs.getString("Name"));
                row.put("Employees", rs.getString("Employees"));
                data.add(row);
            }

            // Find the table in the current view and update it
            StackPane contentArea = (StackPane) ((BorderPane) mainStage.getScene().getRoot()).getCenter();
            for (Node node : contentArea.getChildren()) {
                if (node instanceof VBox) {
                    for (Node child : ((VBox) node).getChildren()) {
                        if (child instanceof TableView) {
                            ((TableView<Map<String, String>>) child).setItems(data);
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            showError("Search Failed", e.getMessage());
        }
    }
    private void handleRemoveDepartment() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Department");
        dialog.setHeaderText("Enter Department Name to Remove:");

        // Updated styling to match add department
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #f3e5f5;" +
                        "-fx-border-color: #ce93d8;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 5;"
        );
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-weight: bold;"
        );
        dialogPane.lookup(".label").setStyle(
                "-fx-text-fill: #6a1b9a;" +
                        "-fx-font-size: 14px;"
        );

        dialog.showAndWait().ifPresent(departmentName -> {
            try {
                // First check if department exists
                if (!departmentExists(departmentName)) {
                    showError("Not Found", "Department '" + departmentName + "' does not exist");
                    return;
                }

                // Then try to remove
               if( removeDepartment(departmentName)) {

                   // Success message (your existing styling)
                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                   alert.setTitle("Department Removed");
                   alert.setHeaderText("Department Removed Successfully");
                   alert.setContentText("The department " + departmentName + " has been removed.");

                   DialogPane alertPane = alert.getDialogPane();
                   alertPane.setStyle("-fx-background-color: linear-gradient(to bottom, #e0eafc, #cfdef3);");
                   alertPane.lookup(".header-panel").setStyle("-fx-background-color: transparent;");
                   alertPane.lookup(".label").setStyle("-fx-font-size: 16px; -fx-font-family: 'Verdana';");

                   alert.showAndWait();
                   refreshDepartmentView();
               }
            }  catch (Exception e) {
                showError("Remove Failed", "Error removing department: " + e.getMessage());
            }
        });
    }



    private boolean removeDepartment(String departmentName) {
        try {
            // Start transaction
            conn.setAutoCommit(false);

            // 1. First check if department exists
            String checkExistSql = "SELECT COUNT(*) FROM departments WHERE department_name = ?";
            try (PreparedStatement checkExistStmt = conn.prepareStatement(checkExistSql)) {
                checkExistStmt.setString(1, departmentName);
                ResultSet rs = checkExistStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    showAlert(Alert.AlertType.ERROR, "Cannot Delete",
                            "Department '" + departmentName + "' does not exist.");
                    conn.rollback();
                    return false;
                }
            }

            // 2. Check if department has active employees
            String checkEmployeesSql = "SELECT COUNT(*) FROM employees WHERE department = ? AND is_active = 1";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmployeesSql)) {
                checkStmt.setString(1, departmentName);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert(Alert.AlertType.ERROR, "Cannot Delete",
                            "Department '" + departmentName + "' has " + rs.getInt(1) +
                                    " active employees assigned. Reassign them first.");
                    conn.rollback();
                    return false;
                }
            }

            // 3. If checks pass, proceed with deletion
            String deleteSql = "DELETE FROM departments WHERE department_name = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, departmentName);
                int affectedRows = deleteStmt.executeUpdate();

                if (affectedRows == 0) {
                    // This shouldn't normally happen since we checked existence earlier
                    showAlert(Alert.AlertType.WARNING, "Delete Warning",
                            "No department was deleted. The department '" + departmentName +
                                    "' may have been removed by another user.");
                    conn.rollback();
                    return false;
                }

                // Success - commit the transaction
                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Delete Successful",
                        "Department '" + departmentName + "' was successfully removed.");
                refreshDepartmentView();
                return true;
            }
        } catch (SQLException e) {
            // Handle any database errors
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
                // Combine original and rollback errors in the message
                e.addSuppressed(rollbackEx);
            }

            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to delete department '" + departmentName + "': " +
                            e.getMessage());
            return false;
        } finally {
            // Reset auto-commit to true regardless of success/failure
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }
    private void updateDepartmentCount(String departmentName, int change) {
        String sql = "UPDATE departments SET employee_count = employee_count + ? " +
                "WHERE department_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, change);
            stmt.setString(2, departmentName);
            stmt.executeUpdate();

            // Refresh department data in the UI
            refreshDepartmentView();
        } catch (SQLException e) {
            showError("Update Count Failed", e.getMessage());
        }
    }

    private void showEmployeePage() {
        // Main layout matching admin/hr style exactly
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top header with gradient and icon (matching admin/hr exactly)
        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: linear-gradient(to right, #6a1b9a, #9c27b0);");

        Label icon = new Label("👤");  // Employee icon
        icon.setStyle("-fx-font-size: 24px;");

        Label title = new Label("Employee Dashboard"); // Changed to just "Employee Dashboard"
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        header.getChildren().addAll(icon, title);
        root.setTop(header);

        // Left navigation (matching admin/hr style exactly)
        VBox navContainer = new VBox(15);
        navContainer.setPadding(new Insets(20));
        navContainer.setStyle("-fx-background-color: #f3e5f5; -fx-border-color: #ce93d8; -fx-border-width: 0 1 0 0;");
        navContainer.setPrefWidth(220);

        // Navigation buttons
        Label empLabel = new Label("Employee Actions");
        empLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6a1b9a; -fx-font-size: 16px;");

        VBox empOptions = new VBox(5);
        createNavButton("🏠 Dashboard", empOptions, () ->
                root.setCenter(createEmployeeDashboardView()));
        createNavButton("⏱️ Punch In", empOptions, () ->
                root.setCenter(createPunchInView()));
        createNavButton("👤 View My Profile", empOptions, () -> {
            Map<String, String> employee = findEmployeeById(currentUsername);
            if (employee != null) {
                root.setCenter(createEmployeeProfileView());
            }
        });
        createNavButton("🔒 Change Password", empOptions, () ->
                root.setCenter(createChangePasswordForm()));

        navContainer.getChildren().addAll(empLabel, empOptions);
        root.setLeft(navContainer);

        // Center content area with shadow (matching admin/hr exactly)
        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        // Set initial content
        contentArea.getChildren().setAll(createEmployeeDashboardView());
        root.setCenter(contentArea);

        // Bottom status bar with back button (matching admin/hr exactly)
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #e1bee7; -fx-border-color: #ce93d8; -fx-border-width: 1 0 0 0;");

        Label statusIcon = new Label("💡");
        Label statusText = new Label("Ready");
        statusText.setStyle("-fx-text-fill: #4a148c;");

        Button backButton = new Button("← Logout");
        backButton.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-background-radius: 15;");
        backButton.setOnAction(e -> LoginView.show(mainStage, this::handleLogin));


        statusBar.getChildren().addAll(statusIcon, statusText);
        HBox.setHgrow(statusText, Priority.ALWAYS);
        statusBar.getChildren().add(backButton);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1100, 750);
        mainStage.setScene(scene);
        mainStage.show();
    }
    private Node createEmployeeDashboardView() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(30));
        dashboard.setAlignment(Pos.TOP_CENTER);
        dashboard.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Welcome message
        String employeeName = getEmployeeName(currentUsername);
        Label welcome = new Label("Welcome, " + employeeName + "!");
        welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");

        // Current date
        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        // Attendance summary for last 30 days
        Map<String, Integer> attendanceSummary = getAttendanceSummary(currentUsername, LocalDate.now().minusDays(30), LocalDate.now());

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        VBox presentBox = createStatBox("Present Days", String.valueOf(attendanceSummary.get("Present")), "#4CAF50");
        VBox absentBox = createStatBox("Absent Days", String.valueOf(attendanceSummary.get("Absent")), "#F44336");
        VBox leaveBox = createStatBox("Leave Days", String.valueOf(attendanceSummary.get("Leave")), "#2196F3");

        statsBox.getChildren().addAll(presentBox, absentBox, leaveBox);

        dashboard.getChildren().addAll(welcome, dateLabel, statsBox);
        return dashboard;
    }

    private Map<String, Integer> getAttendanceSummary(String employeeId, LocalDate from, LocalDate to) {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("Present", 0);
        summary.put("Absent", 0);
        summary.put("Leave", 0);

        String sql = "SELECT status, COUNT(*) as count FROM attendance " +
                "WHERE employee_id = ? AND attendance_date BETWEEN ? AND ? " +
                "GROUP BY status";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                summary.put(status, count);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }

        return summary;
    }private Node createAttendanceSummaryView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        // Date range selection
        DatePicker startDate = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker endDate = new DatePicker(LocalDate.now());

        // Employee filter (for HR/Admin)
        ComboBox<String> employeeCombo = new ComboBox<>();
        populateEmployeeCombo(employeeCombo);

        // Generate button
        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white;");

        // Table setup
        TableView<Map<String, String>> table = new TableView<>();
        setupDailyAttendanceColumns(table); // New column setup method

        // Generate action
        generateBtn.setOnAction(e -> {
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();
            String employeeId = employeeCombo.getValue();

            if (start == null || end == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select date range");
                return;
            }

            if (start.isAfter(end)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Start date must be before end date");
                return;
            }

            List<Map<String, String>> reportData = generateDailyAttendanceData(
                    start, end, employeeId);
            table.setItems(FXCollections.observableArrayList(reportData));
        });

        // Layout
        HBox dateBox = new HBox(10,
                new Label("From:"), startDate,
                new Label("To:"), endDate,
                generateBtn
        );
        dateBox.setAlignment(Pos.CENTER_LEFT);

        container.getChildren().addAll(
                new Label("Daily Attendance Report"),
                new Label("Employee:"), employeeCombo,
                dateBox,
                table
        );

        return container;
    }

    private void setupDailyAttendanceColumns(TableView<Map<String, String>> table) {
        table.getColumns().clear();

        TableColumn<Map<String, String>, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Date")));

        TableColumn<Map<String, String>, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Status")));

        table.getColumns().addAll(dateCol, statusCol);
    }


    private List<Map<String, String>> generateDailyAttendanceData(LocalDate start, LocalDate end, String employeeId) {
        List<Map<String, String>> reportData = new ArrayList<>();
        String sql = "SELECT attendance_date, status FROM attendance " +
                "WHERE employee_id = ? AND attendance_date BETWEEN ? AND ? " +
                "ORDER BY attendance_date";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId != null ? employeeId : currentUsername);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("Date", rs.getDate("attendance_date").toString());
                row.put("Status", rs.getString("status"));
                reportData.add(row);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
        return reportData;
    }




    private Node createEmployeeProfileView() {
        // Get employee data - same as employee detail view
        Map<String, String> employee = getEmployeeProfileData(currentUsername);
        if (employee == null || employee.isEmpty()) {
            return new Label("Error loading profile data");
        }


        // Create detail view similar to employee detail view
        VBox detailBox = new VBox(15);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Personal Info Section
        Label personalTitle = new Label("Personal Information");
        personalTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");

        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(10);
        personalGrid.setVgap(5);
        personalGrid.setPadding(new Insets(10));

        addProfileRow(personalGrid, "Employee ID:", employee.get("ID"), 0);
        addProfileRow(personalGrid, "Name:", employee.get("Name"), 1);
        addProfileRow(personalGrid, "Date of Birth:", employee.get("DOB"), 2);
        addProfileRow(personalGrid, "Phone:", employee.get("Phone"), 3);

        // Employment Info Section
        Label employmentTitle = new Label("Employment Information");
        employmentTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");

        GridPane employmentGrid = new GridPane();
        employmentGrid.setHgap(10);
        employmentGrid.setVgap(5);
        employmentGrid.setPadding(new Insets(10));

        addProfileRow(employmentGrid, "Department:", employee.get("Department"), 0);
        addProfileRow(employmentGrid, "Position:", employee.get("Position"), 1);
        addProfileRow(employmentGrid, "Join Date:", employee.get("Join Date"), 2);
        addProfileRow(employmentGrid, "Education:", employee.get("Education"), 3);
        addProfileRow(employmentGrid, "Salary:", employee.get("Salary"), 4);

        detailBox.getChildren().addAll(
                personalTitle, personalGrid,
                employmentTitle, employmentGrid
        );

        return detailBox;
    }
    private void addProfileRow(GridPane grid, String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold;");
        grid.add(lbl, 0, row);

        Label val = new Label(value != null ? value : "N/A");
        grid.add(val, 1, row);
    }


    private VBox createStatBox(String title, String value, String color) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + color + "20; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }
    private String getEmployeeName(String employeeId) {
        String sql = "SELECT name FROM employees WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to fetch employee name: " + e.getMessage());
        }
        return "Employee"; // Fallback if name can't be retrieved
    }private List<Map<String, String>> getAttendanceRecordsForEmployee(String employeeId, LocalDate from, LocalDate to) {
        List<Map<String, String>> records = new ArrayList<>();
        String sql = "SELECT attendance_date, status FROM attendance " +
                "WHERE employee_id = ? AND attendance_date BETWEEN ? AND ? " +
                "ORDER BY attendance_date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> record = new HashMap<>();
                record.put("Date", rs.getDate("attendance_date").toString());
                record.put("Status", rs.getString("status"));
                records.add(record);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
        return records;
    }private Node createPunchInView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);

        Label title = new Label("Daily Attendance");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button punchBtn = new Button("Punch In");
        punchBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        punchBtn.setPrefSize(200, 40);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px;");

        // Check initial status
        checkPunchInStatus(punchBtn, statusLabel);

        punchBtn.setOnAction(e -> {
            if (recordAttendance(currentUsername, LocalDate.now(), "Present")) {
                statusLabel.setText("Successfully punched in for " + LocalDate.now());
                statusLabel.setStyle("-fx-text-fill: green;");
                punchBtn.setDisable(true);
            } else {
                statusLabel.setText("Failed to record attendance");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        container.getChildren().addAll(title, punchBtn, statusLabel);
        return container;
    }

    private void checkPunchInStatus(Button punchBtn, Label statusLabel) {
        LocalDate today = LocalDate.now();
        if (hasAttendanceRecord(currentUsername, today)) {
            punchBtn.setDisable(true);
            statusLabel.setText("You've already punched in today!");
            statusLabel.setStyle("-fx-text-fill: #6a1b9a;");
        } else if (isOnLeave(currentUsername, today)) {
            punchBtn.setDisable(true);
            statusLabel.setText("You're on leave today - cannot punch in!");
            statusLabel.setStyle("-fx-text-fill: orange;");
        }
    }
    private void populateEmployeeCombo(ComboBox<String> employeeCombo) {
        // Clear existing items
        employeeCombo.getItems().clear();

        // Query active employees
        String sql = "SELECT id, name FROM employees WHERE is_active = 1 ORDER BY name";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Create a map to store id-name pairs
            Map<String, String> employees = new LinkedHashMap<>();

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                employees.put(id, name);
            }

            // Set custom cell factory to show both ID and name
            employeeCombo.setCellFactory(param -> new ListCell<String>() {
                @Override
                protected void updateItem(String id, boolean empty) {
                    super.updateItem(id, empty);
                    setText(empty || id == null ? null : id + " - " + employees.get(id));
                }
            });

            // Set button cell for display
            employeeCombo.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String id, boolean empty) {
                    super.updateItem(id, empty);
                    setText(empty || id == null ? null : id + " - " + employees.get(id));
                }
            });

            // Add all IDs to the combo box
            employeeCombo.getItems().addAll(employees.keySet());

        } catch (SQLException e) {
            showError("Database Error", "Failed to load employees: " + e.getMessage());
        }

        // Set converter for selection value
        employeeCombo.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String id) {
                return id != null ? Boolean.parseBoolean(id + " - " + employeeCombo.getItems().contains(id)) ?
                        employeeCombo.getButtonCell().getText() : "" : "";
            }

            @Override
            public String fromString(String string) {
                return string.split(" - ")[0]; // Extract ID from display string
            }
        });
    }


    private int countAbsences(String employeeId, LocalDate untilDate) {
        String sql = "SELECT COUNT(*) FROM attendance " +
                "WHERE employee_id = ? AND status = 'Absent' " +
                "AND date BETWEEN ? AND ? " +
                "AND DAYOFWEEK(date) NOT IN (1,7)"; // Exclude weekends

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(untilDate.withDayOfYear(1)));
            stmt.setDate(3, Date.valueOf(untilDate));

            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }
    // Helper method to check if attendance record exists for date
    private boolean hasAttendanceRecord(String employeeId, LocalDate date) {
        // Validate inputs first
        if (employeeId == null || employeeId.isEmpty() || date == null) {
            showError("Validation Error", "Invalid employee ID or date");
            return false;
        }

        String sql = "SELECT 1 FROM attendance WHERE employee_id = ? AND attendance_date = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY)) {

            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if record exists
            }
        } catch (SQLException e) {
            // Log the error with more context
            String errorMsg = String.format("Failed to check attendance for %s on %s: %s",
                    employeeId, date, e.getMessage());
            showError("Database Error", errorMsg);
            return false;
        }
    }
    // Helper method to check if employee is on leave for date
    private boolean isOnLeave(String employeeId, LocalDate date) {
        String sql = "SELECT 1 FROM leave_records WHERE employee_id = ? AND leave_date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            showError("Database Error", "Failed to check leave status: " + e.getMessage());
            return false;
        }
    }

    // Updated recordAttendance method

    private Node createRegisterLeaveView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        // Employee selection
        ComboBox<String> employeeCombo = new ComboBox<>();
        populateEmployeeCombo(employeeCombo);

        // Date selection
        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker endDate = new DatePicker(LocalDate.now().plusDays(1));

        // Reason input
        TextArea reasonInput = new TextArea();
        reasonInput.setPromptText("Leave reason...");

        // Submit button
        Button submitBtn = new Button("Register Leave");
        submitBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white;");

        // Status label
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            String employeeId = employeeCombo.getValue();
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();
            String reason = reasonInput.getText();

            if (validateLeaveRequest(employeeId, start, end, reason)) {
                if (registerLeavePeriod(employeeId, start, end, reason)) {
                    statusLabel.setText("Leave registered successfully!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    statusLabel.setText("Failed to register leave.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });

        container.getChildren().addAll(
                new Label("Register Leave Period"),
                new Label("Employee:"), employeeCombo,
                new Label("From:"), startDate,
                new Label("To:"), endDate,
                new Label("Reason:"), reasonInput,
                submitBtn, statusLabel
        );

        return container;
    }


    private boolean validateLeaveRequest(String employeeId, LocalDate start, LocalDate end, String reason) {
        // 1. Basic validation
        if (employeeId == null || employeeId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an employee");
            return false;
        }

        if (start == null || end == null || start.isAfter(end)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid date range");
            return false;
        }

        if (reason == null || reason.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a reason");
            return false;
        }

        // 2. Check for overlapping leave
        try {
            String overlapSql = "SELECT COUNT(*) FROM leave_records " +
                    "WHERE employee_id = ? " +
                    "AND leave_date BETWEEN ? AND ?";
            try (PreparedStatement stmt = conn.prepareStatement(overlapSql)) {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(start));
                stmt.setDate(3, Date.valueOf(end));

                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert(Alert.AlertType.ERROR, "Conflict", "Existing leave in this period");
                    return false;
                }
            }

            // 3. Check available leave days
            int availableDays = getAvailableLeaveDays(employeeId);
            int requestedDays = (int) ChronoUnit.DAYS.between(start, end) + 1;

            if (requestedDays > availableDays) {
                showAlert(Alert.AlertType.WARNING,
                        "Limit Exceeded",
                        String.format("Only %d leave days available (requested %d)",
                                availableDays, requestedDays));
                return false;
            }

            return true;

        } catch (SQLException e) {
            showError("Validation Error", e.getMessage());
            return false;
        }
    }
    private boolean recordAttendance(String employeeId, LocalDate date, String status) {
        try {
            conn.setAutoCommit(false);

            // Check if record exists (update if "Absent", else insert)
            String checkSql = "SELECT status FROM attendance WHERE employee_id = ? AND attendance_date = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, employeeId);
                checkStmt.setDate(2, Date.valueOf(date));
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Update existing record (e.g., "Absent" → "Present")
                    String updateSql = "UPDATE attendance SET status = ? WHERE employee_id = ? AND attendance_date = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, status);
                        updateStmt.setString(2, employeeId);
                        updateStmt.setDate(3, Date.valueOf(date));
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new record
                    String insertSql = "INSERT INTO attendance (employee_id, attendance_date, status) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, employeeId);
                        insertStmt.setDate(2, Date.valueOf(date));
                        insertStmt.setString(3, status);
                        insertStmt.executeUpdate();
                    }
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            showError("Database Error", "Failed to record attendance: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    private boolean registerLeavePeriod(String employeeId, LocalDate start, LocalDate end, String reason) {
        try {
            conn.setAutoCommit(false);

            // 1. Check available leave days
            int availableDays = getAvailableLeaveDays(employeeId);
            int requestedDays = (int) ChronoUnit.DAYS.between(start, end) + 1;

            if (requestedDays > availableDays) {
                showAlert(Alert.AlertType.WARNING,
                        "Limit Exceeded",
                        String.format("Only %d leave days available!", availableDays));
                return false;
            }

            String checkAttendanceSql = "SELECT COUNT(*) FROM attendance " +
                    "WHERE employee_id = ? AND attendance_date BETWEEN ? AND ? " +
                    "AND status = 'Present'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkAttendanceSql)) {
                checkStmt.setString(1, employeeId);
                checkStmt.setDate(2, Date.valueOf(start));
                checkStmt.setDate(3, Date.valueOf(end));
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert(Alert.AlertType.ERROR, "Conflict",
                            "Employee has already punched in for some days in this period!");
                    conn.rollback();
                    return false;
                }
            }

            // 2. Register each day as leave
            String sql = "INSERT INTO leave_records (employee_id, leave_date, reason) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                LocalDate current = start;
                while (!current.isAfter(end)) {
                    stmt.setString(1, employeeId);
                    stmt.setDate(2, Date.valueOf(current));
                    stmt.setString(3, reason);
                    stmt.addBatch();
                    current = current.plusDays(1);
                }
                stmt.executeBatch();
            }

            // 3. Update attendance records
            String updateSql = "INSERT INTO attendance (employee_id, attendance_date, status) VALUES (?, ?, 'Leave') " +
                    "ON DUPLICATE KEY UPDATE status = 'Leave'";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                LocalDate current = start;
                while (!current.isAfter(end)) {
                    stmt.setString(1, employeeId);
                    stmt.setDate(2, Date.valueOf(current));
                    stmt.addBatch();
                    current = current.plusDays(1);
                }
                stmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            showError("Leave Registration Failed", e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }


    private int getAvailableLeaveDays(String employeeId) throws SQLException {
        // Base yearly allowance
        int yearlyAllowance = 10;

        // Get unused days from previous years
        String carryOverSql = "SELECT SUM(yearly_allowance - used_days) FROM leave_balance " +
                "WHERE employee_id = ? AND year < YEAR(CURDATE())";
        int carryOver = 0;
        try (PreparedStatement stmt = conn.prepareStatement(carryOverSql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) carryOver = rs.getInt(1);
        }

        // Get used days this year
        String usedSql = "SELECT COUNT(*) FROM leave_records " +
                "WHERE employee_id = ? AND YEAR(leave_date) = YEAR(CURDATE())";
        int usedDays = 0;
        try (PreparedStatement stmt = conn.prepareStatement(usedSql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) usedDays = rs.getInt(1);
        }

        return yearlyAllowance + carryOver - usedDays;
    }




    private Button createBackButton() {
        Button backButton = new Button("Log Out");
        backButton.setOnAction(e ->LoginView.show(mainStage, this::handleLogin));

        return backButton;
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        return button;
    }
    private void handleAddEmployee() {
        // Main dialog setup
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("➕ Add New Employee");
        dialog.getDialogPane().setStyle("-fx-background-color: #f3e5f5;");

        // Prevent closing unless Cancel is clicked
        dialog.setResultConverter(buttonType -> null);

        // Form Grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        // Employee ID Field (4-5 alphanumeric chars)
        TextField idField = new TextField();
        idField.setPromptText("4-5 letters/numbers");
        idField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[A-Za-z0-9]{0,5}")) {
                idField.setText(oldVal);
            }
        });

        // Name Field (10-20 chars with space)
        TextField nameField = new TextField();
        nameField.setPromptText("First Last (10-20 chars)");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 20 || (newVal.length() > 0 && !newVal.matches("^[A-Za-z ]*$"))) {
                nameField.setText(oldVal);
            }
        });

        // Date of Birth Field (default to current date)
        DatePicker dobPicker = new DatePicker(LocalDate.now());

        // Phone Field (must start with 09, exactly 10 digits)
        TextField phoneField = new TextField();
        phoneField.setPromptText("09xxxxxxxx");
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("09\\d{0,8}") || newVal.length() > 10) {
                phoneField.setText(oldVal);
            }
        });

        // Department ComboBox with refresh button
        ComboBox<String> deptCombo = new ComboBox<>();
        Button refreshDeptBtn = new Button("🔄");
        refreshDeptBtn.setStyle("-fx-background-color: transparent;");
        refreshDeptBtn.setOnAction(e -> loadDepartmentDataOnly(deptCombo));
        HBox deptBox = new HBox(5, deptCombo, refreshDeptBtn);

        // Position ComboBox
        ComboBox<String> positionCombo = new ComboBox<>();
        positionCombo.getItems().addAll("Manager", "Developer", "Cleaner", "Analyst", "Intern");

        // Education ComboBox
        ComboBox<String> educationCombo = new ComboBox<>();
        educationCombo.getItems().addAll("High School", "Bachelor's", "Master's", "PhD");

        // Gender ComboBox (default Female)
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Female", "Male");
        genderCombo.setValue("Female");

        // Join Date (default today)
        DatePicker joinDatePicker = new DatePicker(LocalDate.now());

        // Salary Field
        TextField salaryField = new TextField();
        salaryField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                salaryField.setText(oldVal);
            }
        });

        // Add form rows
        int row = 0;
        addFormRow(grid, "Employee ID* (4-5 chars):", idField, row++);
        addFormRow(grid, "Full Name* (10-20 chars with space):", nameField, row++);
        addFormRow(grid, "Date of Birth:", dobPicker, row++);
        addFormRow(grid, "Department*:", deptBox, row++);
        addFormRow(grid, "Position*:", positionCombo, row++);
        addFormRow(grid, "Education*:", educationCombo, row++);
        addFormRow(grid, "Gender:", genderCombo, row++);
        addFormRow(grid, "Join Date:", joinDatePicker, row++);
        addFormRow(grid, "Salary*:", salaryField, row++);
        addFormRow(grid, "Phone* (09xxxxxxxx):", phoneField, row++);

        // Action Buttons
        ButtonType addButton = new ButtonType("Add Employee", ButtonBar.ButtonData.OK_DONE);
        ButtonType clearButton = new ButtonType("Clear Form", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, clearButton, ButtonType.CANCEL);

        // Style buttons
        Node addBtn = dialog.getDialogPane().lookupButton(addButton);
        addBtn.setStyle("-fx-background-color: #7b1fa2; -fx-text-fill: white; -fx-font-weight: bold;");

        Node clearBtn = dialog.getDialogPane().lookupButton(clearButton);
        clearBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white;");

        // Clear form action
        clearBtn.addEventFilter(ActionEvent.ACTION, e -> {
            // Clear text fields
            idField.clear();
            nameField.clear();
            salaryField.clear();
            phoneField.clear();

            // Reset combo box selections
            deptCombo.getSelectionModel().clearSelection();
            positionCombo.getSelectionModel().clearSelection();
            educationCombo.getSelectionModel().clearSelection();

            // Reset default values
            genderCombo.setValue("Female");
            dobPicker.setValue(LocalDate.now());
            joinDatePicker.setValue(LocalDate.now());

            // Reset styles
            idField.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");
            nameField.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");
            salaryField.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");
            phoneField.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");
            deptCombo.setStyle("-fx-background-radius: 15;");
            positionCombo.setStyle("-fx-background-radius: 15;");
            educationCombo.setStyle("-fx-background-radius: 15;");
            genderCombo.setStyle("-fx-background-radius: 15;");
            dobPicker.setStyle("-fx-background-radius: 15;");
            joinDatePicker.setStyle("-fx-background-radius: 15;");

            e.consume(); // Prevent dialog from closing
        });

        // Add employee action
        addBtn.addEventFilter(ActionEvent.ACTION, e -> {
            if (!validateForm(idField, nameField, deptCombo, positionCombo, educationCombo,
                    phoneField, salaryField)) {
                showError("Validation Error", "Please fill all required fields properly");
                e.consume();
            } else {
                Map<String, String> employee = new HashMap<>();
                employee.put("id", idField.getText().trim());
                employee.put("name", nameField.getText().trim());
                employee.put("department", deptCombo.getValue());
                employee.put("position", positionCombo.getValue());
                employee.put("education", educationCombo.getValue());
                employee.put("sex", genderCombo.getValue()); // Changed to "sex" to match DB
                employee.put("date_of_birth", dobPicker.getValue().toString());
                employee.put("join_date", joinDatePicker.getValue().toString());
                employee.put("salary", salaryField.getText().trim());
                employee.put("phone", phoneField.getText().trim());
                employee.put("password", "emp123"); // Default password

                if (insertEmployee(employee)) {
                    showConfirmation("Success", "✅ Employee added successfully!");
                    clearForm(idField, nameField, deptCombo, positionCombo, educationCombo,
                            genderCombo, dobPicker, joinDatePicker, salaryField, phoneField);
                }
                e.consume(); // Keep form open
            }
        });

        // Load initial department data
        loadDepartmentData(deptCombo);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    // Helper Methods
    private boolean validateForm(TextField idField, TextField nameField,
                                 ComboBox<String> deptCombo, ComboBox<String> positionCombo,
                                 ComboBox<String> educationCombo, TextField phoneField,
                                 TextField salaryField) {
        boolean isValid = true;

        // Validate Employee ID
        if (idField.getText().length() < 4 || idField.getText().length() > 5) {
            idField.setStyle("-fx-border-color: red; -fx-background-radius: 15;");
            isValid = false;
        } else {
            idField.setStyle("-fx-background-radius: 15;");
        }

        // Validate Name
        String name = nameField.getText();
        if (name.length() < 10 || name.length() > 20 || !name.contains(" ")) {
            nameField.setStyle("-fx-border-color: red; -fx-background-radius: 15;");
            isValid = false;
        } else {
            nameField.setStyle("-fx-background-radius: 15;");
        }

        // Validate Department
        if (deptCombo.getValue() == null) {
            deptCombo.setStyle("-fx-border-color: red; -fx-background-radius: 15;");
            isValid = false;
        } else {
            deptCombo.setStyle("-fx-background-radius: 15;");
        }

        // Validate other required fields...

        return isValid;
    }

    private void clearForm(TextField idField, TextField nameField,
                           ComboBox<String> deptCombo, ComboBox<String> positionCombo,
                           ComboBox<String> educationCombo, ComboBox<String> genderCombo,
                           DatePicker joinDatePicker, DatePicker datePicker, TextField salaryField,
                           TextField phoneField) {
        idField.clear();
        nameField.clear();
        deptCombo.getSelectionModel().clearSelection();
        positionCombo.getSelectionModel().clearSelection();
        educationCombo.getSelectionModel().clearSelection();
        genderCombo.setValue("Female");
        joinDatePicker.setValue(LocalDate.now());
        salaryField.clear();
        phoneField.clear();

        // Reset styles
        idField.setStyle("-fx-background-radius: 15;");
        nameField.setStyle("-fx-background-radius: 15;");
        deptCombo.setStyle("-fx-background-radius: 15;");
        // Reset other fields' styles...
    }
    private void loadDepartmentData(ComboBox<String> combo) {
        loadDepartmentDataWithAll(combo);
    }
    private void loadDepartmentDataWithAll(ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        try (
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department_name FROM departments")) {
            comboBox.getItems().add("All Departments"); // Include this option
            while (rs.next()) {
                comboBox.getItems().add(rs.getString("department_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void addFormRow(GridPane grid, String labelText, Node field, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #6a1b9a; -fx-font-weight: bold;");
        grid.add(label, 0, row);
        grid.add(field, 1, row);

        // Style the input field
        if (field instanceof TextField) {
            field.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");
        } else if (field instanceof ComboBox) {
            field.setStyle("-fx-background-radius: 15;");
        }
    }
    private void showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: #f3e5f5;");
        pane.lookup(".content.label").setStyle("-fx-text-fill: #6a1b9a; -fx-font-size: 14px;");

        alert.showAndWait();
    }private void handleRemoveEmployee(String employeeId) {
        // Confirm deletion
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Employee");
        confirmDialog.setContentText("Are you sure you want to delete employee " + employeeId + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteEmployee(employeeId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully!");
                refreshEmployeeTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete employee.");
            }
        }
    }

    private void handleFilterEmployees() {

        List<String> filterOptions = Arrays.asList("Sex", "Department", "Join Date", "Salary");


        ChoiceDialog<String> filterDialog = new ChoiceDialog<>(filterOptions.get(0), filterOptions);
        filterDialog.setTitle("Filter Employees");
        filterDialog.setHeaderText("Choose filter criteria:");
        filterDialog.setContentText("Filter by:");

        filterDialog.showAndWait().ifPresent(filterChoice -> {

            TextInputDialog filterValueDialog = new TextInputDialog();
            filterValueDialog.setTitle("Enter Filter Value");


            if (filterChoice.equals("Sex")) {
                filterValueDialog.setHeaderText("Enter Sex ( Male/Female):");
            } else if (filterChoice.equals("Department")) {
                filterValueDialog.setHeaderText("Enter Department Name:");
            } else if (filterChoice.equals("Join Date")) {
                filterValueDialog.setHeaderText("Enter Join Date (yyyy-mm-dd):");
            } else if (filterChoice.equals("Salary")) {
                filterValueDialog.setHeaderText("Enter Salary (greater than):");
            }

            filterValueDialog.showAndWait().ifPresent(filterValue -> {
                Map<String, String> filters = new HashMap<>();
                filters.put(filterChoice.toLowerCase(), filterValue);

                List<Map<String, String>> filteredEmployees = applyEmployeeFilters(filters);
                displayFilteredEmployees(filteredEmployees);
            });
        });
    }

    private List<Map<String, String>> applyEmployeeFilters(Map<String, String> filters) {
        List<Map<String, String>> filteredEmployees = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, name, department, position, phone_number, sex, join_date, salary " +
                        "FROM employees WHERE is_active = 1 "
        );

        // Build WHERE clause based on filters
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (filters.get("department") != null) {
            conditions.add("department = ?");
            params.add(filters.get("department"));
        }

        if (filters.get("position") != null) {
            conditions.add("position = ?");
            params.add(filters.get("position"));
        }

        if (filters.get("gender") != null) {
            conditions.add("sex = ?");
            params.add(filters.get("gender"));
        }

        if (filters.get("seniority") != null) {
            LocalDate cutoffDate = LocalDate.now();
            switch (filters.get("seniority")) {
                case ">1 Year": cutoffDate = cutoffDate.minusYears(1); break;
                case ">3 Years": cutoffDate = cutoffDate.minusYears(3); break;
                case ">5 Years": cutoffDate = cutoffDate.minusYears(5); break;
                case ">10 Years": cutoffDate = cutoffDate.minusYears(10); break;
            }
            conditions.add("join_date <= ?");
            params.add(Date.valueOf(cutoffDate));
        }

        if (filters.get("salary") != null) {
            switch (filters.get("salary")) {
                case "<10,000": conditions.add("salary < 10000"); break;
                case "10,000-30,000": conditions.add("salary BETWEEN 10000 AND 30000"); break;
                case "30,000-50,000": conditions.add("salary BETWEEN 30000 AND 50000"); break;
                case ">50,000": conditions.add("salary > 50000"); break;
            }
        }

        if (!conditions.isEmpty()) {
            sql.append("AND ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY name");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> employee = new HashMap<>();
                employee.put("ID", rs.getString("id"));
                employee.put("Name", rs.getString("name"));
                employee.put("Department", rs.getString("department"));
                employee.put("Position", rs.getString("position"));
                employee.put("Phone", rs.getString("phone_number"));
                employee.put("Gender", rs.getString("sex"));
                employee.put("Join Date", rs.getString("join_date"));
                employee.put("Salary", rs.getString("salary"));
                filteredEmployees.add(employee);
            }
        } catch (SQLException e) {
            showError("Filter Error", e.getMessage());
        }

        return filteredEmployees;
    }
    private Node createGenerateLeaveView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Employee selection
        ComboBox<String> employeeCombo = new ComboBox<>();
        populateEmployeeCombo(employeeCombo);

        // Date range selection
        DatePicker startDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDate = new DatePicker(LocalDate.now());

        // Generate button
        Button generateBtn = new Button("Generate Leave Report");
        generateBtn.setStyle("-fx-background-color: #6a1b9a; -fx-text-fill: white;");

        // Table setup - make sure it's properly initialized
        TableView<Map<String, String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300); // Set a fixed height to ensure visibility

        // Define columns
        TableColumn<Map<String, String>, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("leave_date")));

        TableColumn<Map<String, String>, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("reason")));

        table.getColumns().addAll(dateCol, reasonCol);

        // Generate action
        generateBtn.setOnAction(e -> {
            String employeeId = employeeCombo.getValue();
            if (employeeId == null || employeeId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select an employee");
                return;
            }

            List<Map<String, String>> leaveRecords = getLeaveRecordsForEmployee(
                    employeeId,
                    startDate.getValue(),
                    endDate.getValue()
            );

            if (leaveRecords.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Records", "No leave records found for the selected period.");
            }

            table.setItems(FXCollections.observableArrayList(leaveRecords));
        });

        // Layout - ensure table is properly contained and visible
        HBox dateBox = new HBox(10,
                new Label("From:"), startDate,
                new Label("To:"), endDate,
                generateBtn
        );
        dateBox.setAlignment(Pos.CENTER_LEFT);

        // Main container layout
        container.getChildren().addAll(
                new Label("Leave Records"),
                new Label("Employee:"), employeeCombo,
                dateBox,
                table
        );

        // Add some spacing and make sure the table expands
        VBox.setVgrow(table, Priority.ALWAYS);
        container.setSpacing(10);

        return container;
    }

    private <T> HBox createPaginationControls(TableView<T> table, ObservableList<T> fullList) {
        HBox paginationBox = new HBox(10);
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.setPadding(new Insets(10, 0, 10, 0));

        Button prevBtn = new Button("Previous");
        Button nextBtn = new Button("Next");
        Label pageLabel = new Label();

        final int[] currentPage = {0};
        final int pageSize = 5;

        Runnable updateTable = () -> {
            int fromIndex = currentPage[0] * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, fullList.size());
            table.setItems(FXCollections.observableArrayList(fullList.subList(fromIndex, toIndex)));
            pageLabel.setText("Page " + (currentPage[0] + 1) + " of " + ((fullList.size() + pageSize - 1) / pageSize));
            prevBtn.setDisable(currentPage[0] == 0);
            nextBtn.setDisable(toIndex >= fullList.size());
        };

        prevBtn.setOnAction(e -> {
            currentPage[0]--;
            updateTable.run();
        });

        nextBtn.setOnAction(e -> {
            currentPage[0]++;
            updateTable.run();
        });

        paginationBox.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        updateTable.run();
        return paginationBox;
    }

    private List<Map<String, String>> getLeaveRecordsForEmployee(String employeeId, LocalDate from, LocalDate to) {
        List<Map<String, String>> records = new ArrayList<>();
        String sql = "SELECT l.leave_date, l.reason, a.status FROM leave_records l " +
                "LEFT JOIN attendance a ON l.employee_id = a.employee_id AND l.leave_date = a.attendance_date " +
                "WHERE l.employee_id = ? AND l.leave_date BETWEEN ? AND ? " +
                "ORDER BY l.leave_date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> record = new HashMap<>();
                record.put("leave_date", rs.getDate("leave_date").toString());
                record.put("reason", rs.getString("reason"));
                record.put("status", rs.getString("status"));
                records.add(record);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
        return records;
    }
    private void displayFilteredEmployees(List<Map<String, String>> employees) {
        // Create a new window to display results
        Stage resultStage = new Stage();
        resultStage.setTitle("Filter Results");

        TableView<Map<String, String>> table = new TableView<>();

        // Define columns
        String[] columns = {"ID", "Name", "Department", "Position", "Join Date", "Seniority", "Salary"};
        for (String col : columns) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(col);
            column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(col)));
            table.getColumns().add(column);
        }

        table.setItems(FXCollections.observableArrayList(employees));

        VBox vbox = new VBox(table);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 800, 600);
        resultStage.setScene(scene);
        resultStage.show();
    }


   private boolean insertEmployee(Map<String, String> empData) {
        String sql = "INSERT INTO employees (id, name, education, department, sex, date_of_birth, join_date, salary, position, phone_number, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, empData.get("id"));
                stmt.setString(2, empData.get("name"));
                stmt.setString(3, empData.get("education"));
                stmt.setString(4, empData.get("department"));
                stmt.setString(5, empData.get("sex"));
                // Convert string dates to SQL Date objects
                stmt.setDate(6, Date.valueOf(empData.get("date_of_birth")));
                stmt.setDate(7, Date.valueOf(empData.get("join_date")));
                stmt.setBigDecimal(8, new BigDecimal(empData.get("salary")));
                stmt.setString(9, empData.get("position"));
                stmt.setString(10, empData.get("phone"));
                stmt.setString(11, "emp123"); // Default password
                stmt.executeUpdate();
            }

            updateDepartmentCount(empData.get("department"), 1);
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            showError("Add Employee Failed", e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }private boolean deleteEmployee(String employeeId) {
        // First check if employee is admin or HR
        String checkRoleSql = "SELECT position FROM employees WHERE id = ? AND is_active = 1";
        try (PreparedStatement checkRoleStmt = conn.prepareStatement(checkRoleSql)) {
            checkRoleStmt.setString(1, employeeId);
            ResultSet rs = checkRoleStmt.executeQuery();
            if (rs.next()) {
                String position = rs.getString("position");
                if (position != null && (position.equalsIgnoreCase("Admin") ||
                        position.equalsIgnoreCase("HR Manager"))) {
                    showAlert(Alert.AlertType.ERROR, "Deletion Restricted",
                            "Cannot delete Admin or HR Manager accounts!");
                    return false;
                }
            }
        } catch (SQLException e) {
            showError("Role Check Failed", e.getMessage());
            return false;
        }

        // Rest of the original delete logic...
        String department = null;
        String getDeptSql = "SELECT department FROM employees WHERE id = ? AND is_active = 1";
        try (PreparedStatement stmt = conn.prepareStatement(getDeptSql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                department = rs.getString("department");
            }
        } catch (SQLException e) {
            showError("Get Department Failed", e.getMessage());
            return false;
        }

        // Original soft delete statements
        String updateEmployeeSQL = "UPDATE employees SET is_active = 0 WHERE id = ?";
        String updateAttendanceSQL = "UPDATE attendance SET is_active = 0 WHERE employee_id = ?";
        String updateLeaveSQL = "UPDATE leave_records SET is_active = 0 WHERE employee_id = ?";
        String updateRoleSQL = "UPDATE roles SET is_active = 0 WHERE employee_id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement psEmployee = conn.prepareStatement(updateEmployeeSQL);
                 PreparedStatement psAttendance = conn.prepareStatement(updateAttendanceSQL);
                 PreparedStatement psLeave = conn.prepareStatement(updateLeaveSQL);
                 PreparedStatement psRole = conn.prepareStatement(updateRoleSQL)) {

                // Verify employee exists and is active
                if (!employeeExists(employeeId)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Employee ID not found.");
                    conn.rollback();
                    return false;
                }

                // Soft delete all related records
                psAttendance.setString(1, employeeId);
                psAttendance.executeUpdate();

                psLeave.setString(1, employeeId);
                psLeave.executeUpdate();

                psRole.setString(1, employeeId);
                psRole.executeUpdate();

                // Soft delete employee
                psEmployee.setString(1, employeeId);
                int rowsUpdated = psEmployee.executeUpdate();

                // Update department count if deletion was successful
                if (rowsUpdated > 0 && department != null) {
                    updateDepartmentCount(department, -1); // Decrement count
                }

                conn.commit();
                return rowsUpdated > 0;

            } catch (SQLException ex) {
                conn.rollback();
                showError("Delete Failed", ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showError("Transaction Error", e.getMessage());
            return false;
        }
    }

    private boolean employeeExists(String employeeId) throws SQLException {
        String sql = "SELECT 1 FROM employees WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    private void handleSearchEmployee() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Advanced Employee Search");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        ComboBox<String> departmentBox = new ComboBox<>();
        ComboBox<String> sexBox = new ComboBox<>();
        ComboBox<String> educationBox = new ComboBox<>();

        // Initialize combo boxes with "None" option
        departmentBox.getItems().addAll("None", "Administration", "Finance", "Marketing", "HR", "IT");
        sexBox.getItems().addAll("None", "Male", "Female");
        educationBox.getItems().addAll("None", "BSc", "MSc", "MBA", "PhD");

        departmentBox.setValue("None");
        sexBox.setValue("None");
        educationBox.setValue("None");

        grid.add(new Label("Employee ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Department:"), 0, 2); grid.add(departmentBox, 1, 2);
        grid.add(new Label("Sex:"), 0, 3); grid.add(sexBox, 1, 3);
        grid.add(new Label("Education:"), 0, 4); grid.add(educationBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                Map<String, String> criteria = new HashMap<>();
                criteria.put("id", idField.getText().trim());
                criteria.put("name", nameField.getText().trim());
                criteria.put("department", departmentBox.getValue().equals("None") ? "" : departmentBox.getValue());
                criteria.put("sex", sexBox.getValue().equals("None") ? "" : sexBox.getValue());
                criteria.put("education", educationBox.getValue().equals("None") ? "" : educationBox.getValue());
                return criteria;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(criteria -> {
            List<Map<String, String>> results = advancedEmployeeSearch(criteria);
            displaySearchResults(results);
        });
    }



    private List<Map<String, String>> advancedEmployeeSearch(Map<String, String> criteria) {
        List<Map<String, String>> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM employees WHERE is_active = 1");
        List<String> params = new ArrayList<>();

        if (!criteria.get("id").isEmpty()) {
            sql.append(" AND id LIKE ?");
            params.add("%" + criteria.get("id") + "%");
        }
        if (!criteria.get("name").isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + criteria.get("name") + "%");
        }
        if (!criteria.get("department").isEmpty()) {
            sql.append(" AND department = ?");
            params.add(criteria.get("department"));
        }
        if (!criteria.get("sex").isEmpty()) {
            sql.append(" AND sex = ?");
            params.add(criteria.get("sex"));
        }
        if (!criteria.get("education").isEmpty()) {
            sql.append(" AND education = ?");
            params.add(criteria.get("education"));
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> employee = new HashMap<>();
                employee.put("id", rs.getString("id"));
                employee.put("name", rs.getString("name"));
                employee.put("education", rs.getString("education"));
                employee.put("department", rs.getString("department"));
                employee.put("sex", rs.getString("sex"));
                employee.put("salary", rs.getString("salary"));
                employee.put("join_date", rs.getString("join_date"));
                employee.put("date_of_birth", rs.getString("date_of_birth"));
                employee.put("position", rs.getString("position"));
                employee.put("phone_number", rs.getString("phone_number"));
                results.add(employee);
            }
        } catch (SQLException e) {
            showError("Search Failed", e.getMessage());
        }

        return results;
    }

    private void showAlert(Alert.AlertType information, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void displaySearchResults(List<Map<String, String>> employees) {
        if (employees.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No employees matched your search criteria.");
            return;
        }

        ListView<String> listView = new ListView<>();
        for (Map<String, String> emp : employees) {
            listView.getItems().add(emp.get("id") + " - " + emp.get("name") + " (" + emp.get("position") + ")");
        }

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String id = selected.split(" - ")[0];
                    Map<String, String> employee = findEmployeeById(id);
                    showEmployeeDetails(employee);
                }
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                new Label("Search Results (double-click to view details):"),
                listView
        );

        Stage stage = new Stage();
        stage.setScene(new Scene(layout, 400, 300));
        stage.show();
    }



    private Map<String, String> findEmployeeById(String id) {
        String sql = "SELECT id, name, education, department, phone_number, salary, join_date, date_of_birth FROM employees WHERE id = ? AND is_active = 1";        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, String> emp = new HashMap<>();
                emp.put("id", rs.getString("id"));
                emp.put("name", rs.getString("name"));
                emp.put("education", rs.getString("education"));
                emp.put("department", rs.getString("department"));
                emp.put("phone_number", rs.getString("phone_number"));
                emp.put("salary", rs.getString("salary"));
                emp.put("join_date", rs.getString("join_date"));
                emp.put("date_of_birth", rs.getString("date_of_birth"));
                return emp;
            }
        } catch (SQLException e) {
            showError("Search by ID Failed", e.getMessage());
        }
        return null;
    }
    public boolean reactivateEmployee(String employeeId) {
        // First get the employee's department before reactivation
        String department = null;
        String getDeptSql = "SELECT department FROM employees WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getDeptSql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                department = rs.getString("department");
            }
        } catch (SQLException e) {
            showError("Get Department Failed", e.getMessage());
            return false;
        }

        // Original reactivation statements
        String reactivateEmployeeSQL = "UPDATE employees SET is_active = 1 WHERE id = ?";
        String reactivateAttendanceSQL = "UPDATE attendance SET is_active = 1 WHERE employee_id = ?";
        String reactivateLeaveSQL = "UPDATE leave_records SET is_active = 1 WHERE employee_id = ?";
        String reactivateRoleSQL = "UPDATE roles SET is_active = 1 WHERE employee_id = ?";

        try {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement psEmployee = conn.prepareStatement(reactivateEmployeeSQL);
                 PreparedStatement psAttendance = conn.prepareStatement(reactivateAttendanceSQL);
                 PreparedStatement psLeave = conn.prepareStatement(reactivateLeaveSQL);
                 PreparedStatement psRole = conn.prepareStatement(reactivateRoleSQL)) {

                // 1. Reactivate employee
                psEmployee.setString(1, employeeId);
                int employeeUpdated = psEmployee.executeUpdate();

                if (employeeUpdated == 0) {
                    conn.rollback();
                    showAlert(Alert.AlertType.ERROR, "Error", "Employee not found");
                    return false;
                }

                // 2. Reactivate related records
                psAttendance.setString(1, employeeId);
                psAttendance.executeUpdate();

                psLeave.setString(1, employeeId);
                psLeave.executeUpdate();

                psRole.setString(1, employeeId);
                psRole.executeUpdate();

                // 3. Update department count if reactivation was successful
                if (department != null) {
                    updateDepartmentCount(department, 1); // Increment count
                }

                conn.commit(); // Commit if all succeed
                return true;

            } catch (SQLException ex) {
                conn.rollback(); // Rollback on any error
                showError("Reactivation Failed", ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }
        } catch (SQLException e) {
            showError("Transaction Error", e.getMessage());
            return false;
        }
    }
    public ObservableList<Map<String, String>> getInactiveEmployees() {
        ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
        String sql = "SELECT id, name, department, position FROM employees WHERE is_active = 0";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("ID", rs.getString("id"));
                row.put("Name", rs.getString("name"));
                row.put("Department", rs.getString("department"));
                row.put("Position", rs.getString("position"));
                data.add(row);
            }
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
        return data;
    }
    private void showEmployeeDetails(Map<String, String> employee) {
        if (employee == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Your profile data could not be loaded.");
            return;
        }
        if (employee == null) {
            showError("Not Found", "No employee found");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Employee Full Details");
        dialog.getDialogPane().setPrefSize(600, 500);

        // Create a TabPane for organization
        TabPane tabPane = new TabPane();

        // Basic Info Tab
        Tab basicTab = new Tab("Basic Info");
        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(10);
        basicGrid.setVgap(10);
        basicGrid.setPadding(new Insets(15));

        String[] basicFields = {
                "ID", "Name", "Department", "Position",
                "Phone", "Education", "Gender"
        };

        for (int i = 0; i < basicFields.length; i++) {
            basicGrid.add(new Label(basicFields[i] + ":"), 0, i);
            basicGrid.add(new Label(employee.get(basicFields[i])), 1, i);
        }

        basicTab.setContent(basicGrid);
        basicTab.setClosable(false);

        // Employment Details Tab
        Tab employmentTab = new Tab("Employment");
        GridPane employmentGrid = new GridPane();
        employmentGrid.setHgap(10);
        employmentGrid.setVgap(10);
        employmentGrid.setPadding(new Insets(15));

        String[] employmentFields = {
                "Salary", "JoinDate", "DOB", "Password"
        };

        for (int i = 0; i < employmentFields.length; i++) {
            employmentGrid.add(new Label(employmentFields[i] + ":"), 0, i);
            employmentGrid.add(new Label(employee.get(employmentFields[i])), 1, i);
        }

        employmentTab.setContent(employmentGrid);
        employmentTab.setClosable(false);

        tabPane.getTabs().addAll(basicTab, employmentTab);
        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Add edit button
        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().add(editButtonType);

        // Handle edit button
        Node editButton = dialog.getDialogPane().lookupButton(editButtonType);
        editButton.addEventFilter(ActionEvent.ACTION, event -> {
            dialog.close();
            showEmployeeDetailDialog(employee); // Reuse our enhanced edit dialog
            event.consume();
        });

        dialog.showAndWait();
    }
    private void addAttendance(String id) {
        String sql = "INSERT INTO attendance (id, attendance_date) VALUES (?, CURDATE())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Punch In Failed", e.getMessage());
        }
    }

    private void addLeave(String employeeId, String leaveDate) {
        String sql = "INSERT INTO leave_records (employee_id, leave_date) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(leaveDate));
            stmt.executeUpdate();
        } catch (SQLException e) {
            showError("Register Leave Failed", e.getMessage());
        }
    }

    private List<String> getAttendanceRecords(String employeeId) {
        List<String> records = new ArrayList<>();
        String sql = "SELECT a.attendance_date FROM attendance a JOIN employees e ON a.employee_id = e.id WHERE a.employee_id = ? AND e.is_active = 1";        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId); // Use employee_id instead of id
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(rs.getDate("attendance_date").toString()); // Use attendance_date instead of date
            }
        } catch (SQLException e) {
            showError("Fetch Attendance Failed", e.getMessage());
        }
        return records;
    }




    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}