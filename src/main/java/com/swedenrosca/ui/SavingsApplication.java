package com.swedenrosca.ui;

import com.swedenrosca.controller.*;
import com.swedenrosca.model.*;
import com.swedenrosca.repository.*;
import com.swedenrosca.seed.DemoDataGenerator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Pos;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.Scanner;
import java.util.Optional;
import java.util.Set;
import org.hibernate.Session;
import com.swedenrosca.service.*;
import com.swedenrosca.controller.UserController;
import com.swedenrosca.controller.ParticipantController;
import com.swedenrosca.controller.PaymentPlanController;

public class SavingsApplication extends Application {
    private final org.hibernate.SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
    private User currentUser;
    private Stage primaryStage;
    private TableView<Payment> paymentsToMakeTable;
    private Label toPayLabel;

    // Controllers
    private final MonthlyPaymentController monthlyPaymentController;
    private final PaymentController paymentController;
    private final RoundController roundController;
    private final GroupController groupController;
    private final UserController userController;
    private final ParticipantController participantController;
    private final AdminMonthPaymentController adminMonthPaymentController;
    private final PaymentPlanController paymentPlanController;

    // Services
    private final UserService userService;
    private final GroupService groupService;
    private final ParticipantService participantService;
    private final PaymentService paymentService;
    private final RoundService roundService;
    private final PaymentOptionService paymentOptionService;
    private final MonthlyPaymentService monthlyPaymentService;
    private final MonthOptionService monthOptionService;
    private final PaymentPlanService paymentPlanService;

    // DemoDataGenerator
    private final DemoDataGenerator demoDataGenerator;

    // Replace SimpleIntegerProperty with IntegerProperty
    private IntegerProperty selectedGroupId = new SimpleIntegerProperty();

    public SavingsApplication() {
        SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
        
        // Initialize repositories
        UserRepository userRepository = new UserRepository();
        GroupRepository groupRepository = new GroupRepository();
        ParticipantRepository participantRepository = new ParticipantRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        PaymentPlanRepository paymentPlanRepository = new PaymentPlanRepository();
        RoundRepository roundRepository = new RoundRepository();
        MonthOptionRepository monthOptionRepository = new MonthOptionRepository();
        PaymentOptionRepository paymentOptionRepository = new PaymentOptionRepository();
        MonthlyPaymentRepository monthlyPaymentRepository = new MonthlyPaymentRepository();

        // Initialize services
        this.userService = new UserService(sessionFactory, userRepository);
        this.groupService = new GroupService(sessionFactory, groupRepository, participantRepository, paymentPlanRepository, roundRepository, paymentRepository, userRepository);
        this.participantService = new ParticipantService(participantRepository);
        this.paymentService = new PaymentService(paymentRepository, groupRepository, participantRepository);
        this.roundService = new RoundService(roundRepository, groupRepository, participantRepository, userRepository);
        this.paymentOptionService = new PaymentOptionService(sessionFactory, paymentOptionRepository);
        this.monthlyPaymentService = new MonthlyPaymentService(monthlyPaymentRepository);
        this.monthOptionService = new MonthOptionService(sessionFactory, monthOptionRepository);
        this.paymentPlanService = new PaymentPlanService(sessionFactory, paymentPlanRepository);

        // Initialize controllers with services
        this.monthlyPaymentController = new MonthlyPaymentController(monthlyPaymentService);
        this.paymentController = new PaymentController(paymentService);
        this.participantController = new ParticipantController(participantService);
        this.roundController = new RoundController(participantService, roundService);
        this.groupController = new GroupController(groupService, userService, participantService, paymentPlanService, roundService, paymentService);
        this.userController = new UserController(userService);
        this.adminMonthPaymentController = new AdminMonthPaymentController();
        this.paymentPlanController = new PaymentPlanController(paymentPlanService);

        // Initialize DemoDataGenerator
        this.demoDataGenerator = new DemoDataGenerator(
            userService,
            groupService,
            participantService,
            paymentService,
            paymentPlanService,
            roundService,
            monthOptionService,
            paymentOptionService
        );
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("SavingsApplication start method reached.");
        this.primaryStage = primaryStage;
        try {
            // Clear all existing data using services
            System.out.println("\n=== Clearing existing data ===");
            paymentService.deleteAll();
            roundService.deleteAll();
            participantService.deleteAll();
            groupService.deleteAll();
            userService.deleteAll();
            monthlyPaymentService.deleteAll();
            paymentOptionService.deleteAll();
            monthOptionService.deleteAll();
            paymentPlanService.deleteAll();
            System.out.println("All existing data cleared.");
           
            // Generate basic demo data (users, payment plans, options)
            System.out.println("\n=== Starting demo data generation ===");
            demoDataGenerator.generateAllDemoData();
            System.out.println("Demo data generation finished.");

            // Verify users were created
            List<User> allUsers = userService.getAllUsers();
            System.out.println("\nVerifying created users:");
            for (User user : allUsers) {
                System.out.println("- " + user.getUsername() + " (ID: " + user.getId() + ", Role: " + user.getRole() + ")");
            }
        } catch (Exception e) {
            System.err.println("Error during startup: " + e.getMessage());
            e.printStackTrace();
        }

        // Add global CSS styling
        String css = """
            .root {
                -fx-background-color: linear-gradient(to bottom, #1a237e, #283593);  /* Deep blue gradient */
            }
            
            .button {
                -fx-background-color: #2196f3;  /* Bright blue */
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10 20;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
            }
            
            .button:hover {
                -fx-background-color: #1976d2;  /* Darker blue */
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 3);
            }
            
            .button:pressed {
                -fx-background-color: #1565c0;  /* Even darker blue */
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);
            }
            
            .table-view {
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #e0e0e0;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);
            }
            
            .table-view .column-header {
                -fx-background-color: #1a237e;  /* Deep blue */
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 12;
            }
            
            .table-view .table-row-cell {
                -fx-background-color: white;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 0 0 1 0;
            }
            
            .table-view .table-row-cell:selected {
                -fx-background-color: #e3f2fd;  /* Light blue */
            }
            
            .table-view .table-row-cell:hover {
                -fx-background-color: #bbdefb;  /* Very light blue */
            }
            
            .tab-pane {
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);
            }
            
            .tab-pane .tab-header-area .tab-header-background {
                -fx-background-color: #1a237e;  /* Deep blue */
            }
            
            .tab-pane .tab {
                -fx-background-color: #3949ab;  /* Medium blue */
                -fx-background-radius: 8 8 0 0;
                -fx-padding: 10 20;
            }
            
            .tab-pane .tab:selected {
                -fx-background-color: white;
            }
            
            .tab-pane .tab .tab-label {
                -fx-text-fill: white;
                -fx-font-weight: bold;
            }
            
            .tab-pane .tab:selected .tab-label {
                -fx-text-fill: #1a237e;  /* Deep blue */
            }
            
            .text-field, .password-field {
                -fx-background-color: white;
                -fx-border-color: #90caf9;  /* Light blue */
                -fx-border-radius: 5;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);
            }
            
            .text-field:focused, .password-field:focused {
                -fx-border-color: #2196f3;  /* Bright blue */
                -fx-effect: dropshadow(gaussian, rgba(33,150,243,0.3), 10, 0, 0, 2);
            }
            
            .label {
                -fx-text-fill: white;
            }
            
            .header-label {
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                -fx-text-fill: white;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);
            }
            
            .vbox, .hbox {
                -fx-background-color: rgba(255, 255, 255, 0.95);  /* Semi-transparent white */
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #90caf9;  /* Light blue */
                -fx-border-width: 1;
                -fx-padding: 20;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);
            }
            
            .combo-box {
                -fx-background-color: white;
                -fx-border-color: #90caf9;  /* Light blue */
                -fx-border-radius: 5;
                -fx-padding: 5;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);
            }
            
            .combo-box .list-cell {
                -fx-text-fill: #1a237e;  /* Deep blue */
                -fx-padding: 8;
            }
            
            .combo-box .list-view {
                -fx-background-color: white;
                -fx-border-color: #90caf9;  /* Light blue */
                -fx-border-radius: 5;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);
            }
            
            .combo-box .list-cell:hover {
                -fx-background-color: #e3f2fd;  /* Light blue */
            }
            
            .combo-box:focused {
                -fx-border-color: #2196f3;  /* Bright blue */
                -fx-effect: dropshadow(gaussian, rgba(33,150,243,0.3), 10, 0, 0, 2);
            }
            
            .alert {
                -fx-background-color: white;
                -fx-border-color: #90caf9;  /* Light blue */
                -fx-border-radius: 8;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3);
            }
            
            .alert .header-panel {
                -fx-background-color: #1a237e;  /* Deep blue */
            }
            
            .alert .content {
                -fx-padding: 20;
            }
            
            .alert .button-bar .button {
                -fx-background-color: #2196f3;  /* Bright blue */
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 8 15;
            }
            
            .alert .button-bar .button:hover {
                -fx-background-color: #1976d2;  /* Darker blue */
            }
            """;
            
        Scene scene = new Scene(new VBox(), 800, 600);
        scene.getStylesheets().add("data:text/css;base64," + java.util.Base64.getEncoder().encodeToString(css.getBytes()));
        
        // First, show the login screen
        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.getStyleClass().add("vbox");
        root.setStyle("-fx-background-color: #1976d2;"); // Solid blue background

        Label titleLabel = new Label("Sweden Savings System");
        titleLabel.getStyleClass().add("header-label");

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setMaxWidth(400);
        formBox.getStyleClass().add("vbox");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        buttonBox.getChildren().addAll(loginButton, registerButton);

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        registerButton.setOnAction(e -> showRegistrationScreen(null)); // You may want to update registration as well

        formBox.getChildren().addAll(
            new Label("Login to Your Account"),
            usernameField,
            passwordField,
            buttonBox
        );

        root.getChildren().addAll(
            titleLabel,
            formBox
        );

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Sweden Savings - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password", Alert.AlertType.ERROR);
            return;
        }

        try {
            System.out.println("\n=== Login Attempt ===");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            // Check if user exists
            User user = userService.getUserByUsername(username);
            System.out.println("User found: " + (user != null ? "Yes" : "No"));
            
            if (user != null) {
                System.out.println("User details:");
                System.out.println("- Username: " + user.getUsername());
                System.out.println("- Role: " + user.getRole());
                System.out.println("- Password match: " + user.getPassword().equals(password));
            }

            if (user != null && user.getPassword().equals(password)) {
                currentUser = user;
                showMainMenu();
            } else {
                String errorMessage = "Invalid credentials";
                if (user == null) {
                    errorMessage = "User not found";
                } else if (!user.getPassword().equals(password)) {
                    errorMessage = "Invalid password";
                }
                showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Login failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showRegistrationScreen(Role role) {
        if (role == null) {
            showAlert("Error", "Please select a role first", Alert.AlertType.ERROR);
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField mobileField = new TextField();
        mobileField.setPromptText("Mobile Number");
        TextField personalNumberField = new TextField();
        personalNumberField.setPromptText("Personal Number");
        TextField bankAccountField = new TextField();
        bankAccountField.setPromptText("Bank Account");
        TextField clearingNumberField = new TextField();
        clearingNumberField.setPromptText("Clearing Number");

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back to Login");

        registerButton.setOnAction(e -> {
            try {
                User newUser = new User(
                    usernameField.getText(),
                    passwordField.getText(),
                    emailField.getText(),
                    personalNumberField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    mobileField.getText(),
                    bankAccountField.getText(),
                    clearingNumberField.getText(),
                    new java.math.BigDecimal("0"), // Default monthly contribution
                    0, // Default number of months
                    role
                );
                userService.createUser(newUser);
                showAlert("Success", "Registration successful", Alert.AlertType.INFORMATION);
                showLoginScreen();
            } catch (Exception ex) {
                showAlert("Error", "Invalid input: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        backButton.setOnAction(e -> showLoginScreen());

        // Create a scrollable container for the form
        ScrollPane scrollPane = new ScrollPane();
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(20));
        formBox.setMaxWidth(400);

        // Add all fields with labels to the form
        formBox.getChildren().addAll(
            new Label("Register New " + role),
            new Label("Username:"),
            usernameField,
            new Label("Password:"),
            passwordField,
            new Label("Email:"),
            emailField,
            new Label("First Name:"),
            firstNameField,
            new Label("Last Name:"),
            lastNameField,
            new Label("Mobile Number:"),
            mobileField,
            new Label("Personal Number:"),
            personalNumberField,
            new Label("Bank Account:"),
            bankAccountField,
            new Label("Clearing Number:"),
            clearingNumberField,
            registerButton,
            backButton
        );

        scrollPane.setContent(formBox);
        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root, 500, 700);
        primaryStage.setTitle("Sweden Savings - Registration");
        primaryStage.setScene(scene);
    }

    private void showMainMenu() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        switch (currentUser.getRole()) {
            case ADMIN -> {
                tabPane.getTabs().addAll(
                    createDashboardTab(),  // New combined dashboard tab
                    createUserManagementTab(),  // Keep user management separate
                    createMonthPaymentManagementTab()  // New tab for month and payment management
                );
            }
            case USER -> {
                tabPane.getTabs().addAll(
                    createUserDashboardTab(),  // New combined user dashboard
                    createUserProfileTab()  // Keep profile separate
                );
            }
        }

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginScreen());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("vbox");
        root.setStyle("-fx-background-color: #1976d2;");

        Label welcomeLabel = new Label("Welcome, " + currentUser.getUsername());
        welcomeLabel.getStyleClass().add("header-label");

        root.getChildren().addAll(
            welcomeLabel,
            tabPane,
            logoutButton
        );

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Sweden Savings - " + currentUser.getRole());
        primaryStage.setScene(scene);
    }

    private Tab createDashboardTab() {
        Tab tab = new Tab("Dashboard");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create a header section
        Label headerLabel = new Label("Admin Dashboard");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create action buttons section
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        actionButtonsBox.setPadding(new Insets(10));
        
        Button showAllGroupsBtn = new Button("Show All Groups");
        Button showActiveGroupsBtn = new Button("Show Active Groups");
        Button showPendingGroupsBtn = new Button("Show Pending Approval");
        Button activateAllPendingBtn = new Button("Activate All Pending");
        Button viewPaymentsBtn = new Button("View All Payments");
        Button refreshPaymentBtn = new Button("Refresh");
        Button clearTableBtn = new Button("Clear Table");
        
        actionButtonsBox.getChildren().addAll(
            showAllGroupsBtn,
            showActiveGroupsBtn,
            showPendingGroupsBtn,
            activateAllPendingBtn,
            viewPaymentsBtn,
            refreshPaymentBtn,
            clearTableBtn
        );

        // Create filter section
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search groups...");
        searchField.setPrefWidth(300);
        
        filterBox.getChildren().addAll(
            new Label("Search:"),
            searchField
        );

        // Create main table
        TableView<Group> mainTable = new TableView<>();
        mainTable.setPrefHeight(400); // Increased from default to 400
        
        // Basic columns
        TableColumn<Group, String> groupNameCol = new TableColumn<>("Group Name");
        TableColumn<Group, String> statusCol = new TableColumn<>("Status");
        TableColumn<Group, Integer> memberCountCol = new TableColumn<>("Members");
        TableColumn<Group, String> contributionCol = new TableColumn<>("Monthly Contribution");
        TableColumn<Group, String> paidAmountCol = new TableColumn<>("Paid Amount");
        
        // Set up cell value factories
        groupNameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGroupName()));
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        memberCountCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            return new SimpleIntegerProperty(participantService.getByGroup(group).size()).asObject();
        });
        contributionCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMonthlyContribution().toString() + " SEK"));
        paidAmountCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            // Get all payments for this group
            List<Payment> payments = paymentService.getByGroup(group);
            // Sum all PAID payments
            java.math.BigDecimal paidSum = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            return new SimpleStringProperty(paidSum + " SEK");
        });
        paidAmountCol.setPrefWidth(150);

        mainTable.getColumns().addAll(groupNameCol, statusCol, memberCountCol, contributionCol, paidAmountCol);

        // Add action buttons for selected group
        HBox selectedGroupActions = new HBox(10);
        Button activateSelectedBtn = new Button("Activate Selected Group");
        Button viewDetailsBtn = new Button("View Details");
        selectedGroupActions.getChildren().addAll(activateSelectedBtn, viewDetailsBtn);

        // Add all components to content
        content.getChildren().addAll(
            headerLabel,
            actionButtonsBox,
            filterBox,
            mainTable,
            selectedGroupActions
        );

        // Set up button handlers
        showAllGroupsBtn.setOnAction(e -> {
            mainTable.getItems().clear();
            mainTable.getItems().addAll(groupService.getAllGroups());
        });

        showActiveGroupsBtn.setOnAction(e -> {
            mainTable.getItems().clear();
            mainTable.getItems().addAll(groupService.getAllActiveGroups());
        });

        showPendingGroupsBtn.setOnAction(e -> {
            mainTable.getItems().clear();
            mainTable.getItems().addAll(groupService.getAllPendingApprovalGroups());
        });

        activateAllPendingBtn.setOnAction(e -> {
            List<Group> pendingGroups = groupService.getAllPendingApprovalGroups();
            for (Group group : pendingGroups) {
                activateGroup(group);
            }
            showAlert("Success", "Activated " + pendingGroups.size() + " groups", Alert.AlertType.INFORMATION);
            refreshMainTable(mainTable, "All");
        });

        activateSelectedBtn.setOnAction(e -> {
            Group selectedGroup = mainTable.getSelectionModel().getSelectedItem();
            if (selectedGroup == null) {
                showAlert("Error", "Please select a group first", Alert.AlertType.ERROR);
                return;
            }
            activateGroup(selectedGroup);
            refreshMainTable(mainTable, "All");
        });

        viewDetailsBtn.setOnAction(e -> {
            Group selectedGroup = mainTable.getSelectionModel().getSelectedItem();
            if (selectedGroup == null) {
                showAlert("Error", "Please select a group first", Alert.AlertType.ERROR);
                return;
            }
            showGroupDetails(selectedGroup);
        });

        viewPaymentsBtn.setOnAction(e -> showAllPaymentsDialog());

        refreshPaymentBtn.setOnAction(e -> refreshMainTable(mainTable, "All"));

        // Add clear table button handler
        clearTableBtn.setOnAction(e -> {
            mainTable.getItems().clear();
        });

        // Set up search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                refreshMainTable(mainTable, "All");
            } else {
                mainTable.getItems().removeIf(group -> 
                    !group.getGroupName().toLowerCase().contains(newVal.toLowerCase()));
            }
        });

        tab.setContent(content);
        return tab;
    }

    private void showGroupDetails(Group group) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Group Details");
        dialog.setHeaderText("Details for " + group.getGroupName());
        dialog.getDialogPane().setPrefSize(1000, 800); // Increased size

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Add group details
        content.getChildren().addAll(
            new Label("Group Name: " + group.getGroupName()),
            new Label("Status: " + group.getStatus()),
            new Label("Members: " + participantService.getByGroup(group).size()),
            new Label("Total Amount: " + group.getTotalAmount()),
            new Label("Monthly Contribution: " + group.getMonthlyContribution())
        );

        // Add participants table
        TableView<Participant> participantsTable = new TableView<>();
        TableColumn<Participant, String> usernameCol = new TableColumn<>("Username");
        TableColumn<Participant, Integer> turnOrderCol = new TableColumn<>("Turn Order");
        
        // Set column widths
        usernameCol.setPrefWidth(200);
        turnOrderCol.setPrefWidth(150);
        
        usernameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUser().getUsername()));
        turnOrderCol.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getTurnOrder()).asObject());

        participantsTable.getColumns().addAll(usernameCol, turnOrderCol);
        participantsTable.getItems().addAll(participantService.getByGroup( group));

        content.getChildren().add(new Label("Participants:"));
        content.getChildren().add(participantsTable);

        // Add payments table
        TableView<Payment> paymentsTable = createPaymentsTable();
        paymentsTable.getItems().addAll(paymentService.getByGroup(group));

        content.getChildren().add(new Label("Payments:"));
        content.getChildren().add(paymentsTable);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    private void activateGroup(Group group) {
        try {
            int maxMembers = group.getMaxMembers();
            LocalDateTime groupStart = group.getStartDate() != null ? group.getStartDate() : LocalDateTime.now();
            LocalDateTime groupEnd = groupStart.plusMonths(maxMembers);

            // Create a round for each turn/order
            List<Round> rounds = new ArrayList<>();
            for (int i = 1; i <= maxMembers; i++) {
                Round round = new Round();
                round.setGroup(group);
                round.setRoundNumber(i);
                round.setStatus(RoundStatus.PENDING_APPROVAL);
                round.setStartDate(groupStart.plusMonths(i - 1));
                round.setEndDate(groupStart.plusMonths(i));
                round.setAmount(group.getMonthlyContribution());
                roundService.createRound(round);
                rounds.add(round);
            }

            // Update group status to ACTIVE
            group.setStatus(GroupStatus.ACTIVE);
            groupService.updateGroup(group);

            // Get all participants sorted by turn order
            List<Participant> participants = participantService.getByGroup( group);
            participants.sort((p1, p2) -> Integer.compare(p1.getTurnOrder(), p2.getTurnOrder()));

            // For each round, set the winner and create payments for all participants
            for (Round round : rounds) {
                int roundOrder = round.getRoundNumber();
                // Set winner for this round (participant with matching turn order)
                for (Participant participant : participants) {
                    if (participant.getTurnOrder() == roundOrder) {
                        round.setWinnerParticipant(participant);
                        round.setStatus(RoundStatus.ACTIVE);
                        roundService.updateRound(round);
                    }
                }
                // Create payments for all participants for this round
                for (Participant participant : participants) {
                    Payment payment = new Payment();
                    payment.setGroup(group);
                    payment.setCreator(participant.getUser());
                    payment.setAmount(group.getMonthlyContribution());
                    payment.setStatus(PaymentStatus.PENDING);
                    payment.setPaymentBy(PaymentBy.USER_PAYMENT);
                    payment.setCreatedAt(LocalDateTime.now());
                    payment.setRound(round);
                    payment.setDueDate(round.getStartDate().plusDays(5)); // Example: due 5 days after round start
                    payment.setPaymentPlan(group.getPaymentPlan()); // Add payment plan
                    paymentService.createPayment(payment);
                }
            }

            showAlert("Success", "Group " + group.getGroupName() + " activated successfully", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Error", "Failed to activate group: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshMainTable(TableView<Group> table, String status) {
        List<Group> groups;
        switch (status) {
            case "Active" -> groups = groupService.getAllActiveGroups();
            case "Pending" -> groups = groupService.getAllPendingApprovalGroups();
            case "Waiting" -> groups = groupService.getAllWaitingForMembersGroups();
            default -> groups = groupService.getAllActiveGroups();
        }
        table.getItems().clear();
        table.getItems().addAll(groups);
    }

    private Tab createUserManagementTab() {
        Tab tab = new Tab("User Management");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create header
        Label headerLabel = new Label("User Management");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create buttons section
        HBox buttonBox = new HBox(10);
        Button viewAllUsersBtn = new Button("View All Users");
        Button manageUserRolesBtn = new Button("Manage User Roles");
        Button clearUsersBtn = new Button("Clear Table");
        buttonBox.getChildren().addAll(viewAllUsersBtn, manageUserRolesBtn, clearUsersBtn);

        // Create table
        TableView<User> userTable = new TableView<>();
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getUsername());
        });

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getRole().toString());
        });

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getEmail());
        });

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getFirstName() + " " + user.getLastName());
        });

        userTable.getColumns().addAll(usernameCol, roleCol, emailCol, nameCol);

        // Add button handlers
        viewAllUsersBtn.setOnAction(e -> {
            try {
                List<User> allUsers = userService.getAllUsers();
                userTable.getItems().clear();
                userTable.getItems().addAll(allUsers);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load users: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        manageUserRolesBtn.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                showAlert("Error", "Please select a user first", Alert.AlertType.ERROR);
                return;
            }

            Dialog<Role> dialog = new Dialog<>();
            dialog.setTitle("Change User Role");
            dialog.setHeaderText("Select new role for user: " + selectedUser.getUsername());

            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            ComboBox<Role> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll(Role.values());
            roleComboBox.setValue(selectedUser.getRole());

            dialog.getDialogPane().setContent(roleComboBox);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    return roleComboBox.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newRole -> {
                try {
                    selectedUser.setRole(newRole);
                    userService.updateUser(selectedUser);
                    showAlert("Success", "User role updated", Alert.AlertType.INFORMATION);
                    viewAllUsersBtn.fire(); // Refresh the table
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update user role: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        // Add clear button handler
        clearUsersBtn.setOnAction(e -> userTable.getItems().clear());

        // Add all components to the content
        content.getChildren().addAll(
            headerLabel,
            buttonBox,
            userTable
        );

        tab.setContent(content);
        return tab;
    }

    private Tab createUserDashboardTab() {
        Tab tab = new Tab("My Dashboard");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create header
        Label headerLabel = new Label("My Groups and Payments");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create summary section
        HBox summaryBox = new HBox(20);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20px; -fx-border-color: #ddd; -fx-border-width: 1px;");
        summaryBox.setPrefHeight(100); // Reduced height from 150 to 100
        
        // Create summary sections
        VBox toPayBox = new VBox(5); // Reduced spacing from 10 to 5
        toPayBox.setAlignment(Pos.CENTER);
        Label toPayHeader = new Label("To Pay");
        toPayHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;"); // Reduced font size
        toPayLabel = new Label("0 SEK");
        toPayLabel.setStyle("-fx-font-size: 14px;"); // Reduced font size
        toPayBox.getChildren().addAll(toPayHeader, toPayLabel);
        
        VBox toReceiveBox = new VBox(5);
        toReceiveBox.setAlignment(Pos.CENTER);
        Label toReceiveHeader = new Label("To Receive");
        toReceiveHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label toReceiveLabel = new Label("0 SEK");
        toReceiveLabel.setStyle("-fx-font-size: 14px;");
        toReceiveBox.getChildren().addAll(toReceiveHeader, toReceiveLabel);
        
        VBox nextPaymentBox = new VBox(5);
        nextPaymentBox.setAlignment(Pos.CENTER);
        Label nextPaymentHeader = new Label("Next Payment");
        nextPaymentHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label nextPaymentLabel = new Label("None");
        nextPaymentLabel.setStyle("-fx-font-size: 14px;");
        nextPaymentBox.getChildren().addAll(nextPaymentHeader, nextPaymentLabel);
        
        VBox nextReceiptBox = new VBox(5);
        nextReceiptBox.setAlignment(Pos.CENTER);
        Label nextReceiptHeader = new Label("Next Receipt");
        nextReceiptHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label nextReceiptLabel = new Label("None");
        nextReceiptLabel.setStyle("-fx-font-size: 14px;");
        nextReceiptBox.getChildren().addAll(nextReceiptHeader, nextReceiptLabel);
        
        summaryBox.getChildren().addAll(toPayBox, toReceiveBox, nextPaymentBox, nextReceiptBox);

        // Create action buttons section
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        actionButtonsBox.setPadding(new Insets(10));
        
        Button joinGroupBtn = new Button("Join a Group");
        Button viewAllGroupsBtn = new Button("View All Groups");
        Button refreshDashboardBtn = new Button("Refresh Dashboard");
        
        // Set consistent button sizes
        joinGroupBtn.setPrefWidth(150);
        viewAllGroupsBtn.setPrefWidth(150);
        refreshDashboardBtn.setPrefWidth(150);
        
        // Set consistent button heights
        joinGroupBtn.setPrefHeight(30);
        viewAllGroupsBtn.setPrefHeight(30);
        refreshDashboardBtn.setPrefHeight(30);
        
        actionButtonsBox.getChildren().addAll(
            joinGroupBtn,
            viewAllGroupsBtn,
            refreshDashboardBtn
        );

        // Create groups table
        TableView<Group> groupsTable = new TableView<>();
        groupsTable.setPrefHeight(400); // Increased from 300 to 400
        
        TableColumn<Group, String> groupNameCol = new TableColumn<>("Group Name");
        TableColumn<Group, String> statusCol = new TableColumn<>("Status");
        TableColumn<Group, String> contributionCol = new TableColumn<>("Monthly Contribution");
        TableColumn<Group, String> turnOrderCol = new TableColumn<>("Your Turn");

        // Set column widths
        groupNameCol.setPrefWidth(200);
        statusCol.setPrefWidth(150);
        contributionCol.setPrefWidth(150);
        turnOrderCol.setPrefWidth(100);

        groupNameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGroupName()));
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        contributionCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMonthlyContribution().toString() + " SEK"));
        turnOrderCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            List<Participant> participants = participantService.getByGroup(group);
            for (Participant p : participants) {
                if (p.getUser().getId().equals(currentUser.getId())) {
                    return new SimpleStringProperty(String.valueOf(p.getTurnOrder()));
                }
            }
            return new SimpleStringProperty("-");
        });

        groupsTable.getColumns().addAll(groupNameCol, statusCol, contributionCol, turnOrderCol);

        // Create payments to make table
        paymentsToMakeTable = new TableView<>();
        paymentsToMakeTable.setPrefHeight(400); // Increased from 300 to 400
        
        TableColumn<Payment, String> groupNameCol2 = new TableColumn<>("Group");
        TableColumn<Payment, String> amountCol = new TableColumn<>("Amount");
        TableColumn<Payment, String> dueDateCol = new TableColumn<>("Due Date");
        TableColumn<Payment, String> statusCol2 = new TableColumn<>("Status");
        TableColumn<Payment, Void> actionCol = new TableColumn<>("Action");

        groupNameCol2.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGroup().getGroupName()));
        amountCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAmount().toString() + " SEK"));
        dueDateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        statusCol2.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPaymentStatus().toString()));

        // Add Pay button to action column
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button payButton = new Button("Pay");
            
            {
                payButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    handlePayment(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Payment payment = getTableView().getItems().get(getIndex());
                    if (payment.getPaymentStatus() != PaymentStatus.PAID) {
                        setGraphic(payButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        paymentsToMakeTable.getColumns().addAll(groupNameCol2, amountCol, dueDateCol, statusCol2, actionCol);

        // Create payments to receive table
        TableView<Payment> paymentsToReceiveTable = new TableView<>();
        paymentsToReceiveTable.setPrefHeight(400); // Increased from 300 to 400
        
        TableColumn<Payment, String> groupNameCol3 = new TableColumn<>("Group");
        TableColumn<Payment, String> amountCol2 = new TableColumn<>("Amount");
        TableColumn<Payment, String> dueDateCol2 = new TableColumn<>("Due Date");
        TableColumn<Payment, String> statusCol3 = new TableColumn<>("Status");

        groupNameCol3.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGroup().getGroupName()));
        amountCol2.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAmount().toString() + " SEK"));
        dueDateCol2.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString()));
        statusCol3.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPaymentStatus().toString()));

        paymentsToReceiveTable.getColumns().addAll(groupNameCol3, amountCol2, dueDateCol2, statusCol3);

        // Load user's groups
        List<Group> userGroups = groupService.getByMember(currentUser.getId());
        groupsTable.getItems().addAll(userGroups);

        // Create refresh button with icon
        refreshDashboardBtn.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
        refreshDashboardBtn.setOnAction(e -> {
            // Refresh groups
            groupsTable.getItems().clear();
            List<Group> freshUserGroups = groupService.getByMember(currentUser.getId());
            groupsTable.getItems().addAll(freshUserGroups);
            
            // Refresh payments
            paymentsToMakeTable.getItems().clear();
            paymentsToReceiveTable.getItems().clear();
            
            // Get all payments for the user's groups
            List<Participant> userParticipants = participantService.getByUser(currentUser);
            Set<Payment> userPayments = new HashSet<>();
            for (Participant participant : userParticipants) {
                userPayments.addAll(paymentService.getByGroupAndParticipant(participant.getGroup(), participant));
            }
            
            // Split payments into to make and to receive
            List<Payment> paymentsToMake = userPayments.stream()
                .filter(p -> p.getCreator().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
                
            List<Payment> paymentsToReceive = userPayments.stream()
                .filter(p -> p.getRound() != null && 
                           p.getRound().getWinnerParticipant() != null && 
                           p.getRound().getWinnerParticipant().getUser().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

            paymentsToMakeTable.getItems().addAll(paymentsToMake);
            paymentsToReceiveTable.getItems().addAll(paymentsToReceive);
            
            // Update summary information
            BigDecimal totalToPay = paymentsToMake.stream()
                .filter(p -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime paymentDate = p.getDueDate();
                    // Check if payment is for current month
                    return paymentDate.getYear() == now.getYear() && 
                           paymentDate.getMonth() == now.getMonth() &&
                           p.getPaymentStatus() != PaymentStatus.PAID;
                })
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalToReceive = paymentsToReceive.stream()
                .filter(p -> {
                    // Only get payments for current month that aren't paid yet
                    LocalDateTime now = LocalDateTime.now();
                    return p.getDueDate().getMonth() == now.getMonth() && 
                           p.getPaymentStatus() != PaymentStatus.PAID;
                })
                .map(p -> p.getGroup().getTotalAmount()) // Get the total amount for the group
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            Optional<Payment> nextPayment = paymentsToMake.stream()
                .filter(p -> p.getPaymentStatus() != PaymentStatus.PAID)
                .min(Comparator.comparing(Payment::getDueDate));
                
            Optional<Payment> nextReceipt = paymentsToReceive.stream()
                .filter(p -> p.getPaymentStatus() != PaymentStatus.PAID)
                .min(Comparator.comparing(Payment::getDueDate));
                
            toPayLabel.setText(totalToPay + " SEK");
            toReceiveLabel.setText(totalToReceive + " SEK");
            nextPaymentLabel.setText(nextPayment.isPresent() ? 
                nextPayment.get().getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "None");
            nextReceiptLabel.setText(nextReceipt.isPresent() ? 
                nextReceipt.get().getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "None");
        });

        // Add button handlers
        joinGroupBtn.setOnAction(e -> showJoinGroupDialog());
        
        viewAllGroupsBtn.setOnAction(e -> {
            groupsTable.getItems().clear();
            groupsTable.getItems().addAll(groupService.getByMember(currentUser.getId()));
        });

        // Add all components to content
        content.getChildren().addAll(
            headerLabel,
            summaryBox,
            actionButtonsBox,
            new Label("My Groups"),
            groupsTable,
            new Label("Payments to Make"),
            paymentsToMakeTable,
            new Label("Payments to Receive"),
            paymentsToReceiveTable
        );

        // Initial load of payments
        refreshDashboardBtn.fire();

        tab.setContent(content);
        return tab;
    }

    private void showJoinGroupDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Join a Group");
        dialog.setHeaderText("Select your preferences");

        // Create the custom dialog content
        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(20));

        // Month options
        Label monthLabel = new Label("Select Number of Months:");
        ComboBox<MonthOption> monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll(monthOptionService.getAll());
        monthComboBox.setConverter(new StringConverter<MonthOption>() {
            @Override
            public String toString(MonthOption option) {
                return option != null ? option.getMonthsCount() + " months" : "";
            }

            @Override
            public MonthOption fromString(String string) {
                return null;
            }
        });

        // Payment options
        Label paymentLabel = new Label("Select Monthly Payment:");
        ComboBox<PaymentOption> paymentComboBox = new ComboBox<>();
        paymentComboBox.getItems().addAll(paymentOptionService.getAll());
        paymentComboBox.setConverter(new StringConverter<PaymentOption>() {
            @Override
            public String toString(PaymentOption option) {
                return option != null ? option.getMonthlyPayment() + " SEK" : "";
            }

            @Override
            public PaymentOption fromString(String string) {
                return null;
            }
        });

        // Turn order selection
        Label turnOrderLabel = new Label("Select Preferred Turn Order:");
        ComboBox<Integer> turnOrderComboBox = new ComboBox<>();
        turnOrderComboBox.setPromptText("Select turn order");
        
        // Update turn order options when month option changes
        monthComboBox.setOnAction(e -> {
            MonthOption selected = monthComboBox.getValue();
            if (selected != null) {
                turnOrderComboBox.getItems().clear();
                for (int i = 1; i <= selected.getMonthsCount(); i++) {
                    turnOrderComboBox.getItems().add(i);
                }
            }
        });

        // Update turn order options when payment option changes
        paymentComboBox.setOnAction(e -> {
            MonthOption selectedMonth = monthComboBox.getValue();
            PaymentOption selectedPayment = paymentComboBox.getValue();
            
            if (selectedMonth != null && selectedPayment != null) {
                // Create temporary payment plan to find matching groups
                PaymentPlan tempPlan = new PaymentPlan();
                tempPlan.setMonthsCount(selectedMonth.getMonthsCount());
                tempPlan.setMonthlyPayment(java.math.BigDecimal.valueOf(selectedPayment.getMonthlyPayment()));
                
                // Find matching groups
                List<Group> matchingGroups = groupService.findAvailableGroupsByPaymentPlan( tempPlan, GroupStatus.WAITING_FOR_MEMBERS);
                
                if (!matchingGroups.isEmpty()) {
                    // If there's an existing group, show only available turn orders
                    Group existingGroup = matchingGroups.get(0);
                    List<Participant> existingParticipants = participantService.getByGroup(existingGroup);
                    Set<Integer> takenTurnOrders = existingParticipants.stream()
                        .map(Participant::getTurnOrder)
                        .collect(Collectors.toSet());
                    
                    turnOrderComboBox.getItems().clear();
                    for (int i = 1; i <= selectedMonth.getMonthsCount(); i++) {
                        if (!takenTurnOrders.contains(i)) {
                            turnOrderComboBox.getItems().add(i);
                        }
                    }
                    
                    if (turnOrderComboBox.getItems().isEmpty()) {
                        showAlert("Information", "This group is full. Please select different options.", Alert.AlertType.INFORMATION);
                    }
                } else {
                    // If no existing group, show all turn orders
                    turnOrderComboBox.getItems().clear();
                    for (int i = 1; i <= selectedMonth.getMonthsCount(); i++) {
                        turnOrderComboBox.getItems().add(i);
                    }
                }
            }
        });

        // Add buttons
        ButtonType joinButtonType = new ButtonType("Join", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(joinButtonType, ButtonType.CANCEL);

        // Add components to content
        dialogContent.getChildren().addAll(
            monthLabel,
            monthComboBox,
            paymentLabel,
            paymentComboBox,
            turnOrderLabel,
            turnOrderComboBox
        );

        dialog.getDialogPane().setContent(dialogContent);

        // --- Refactored join logic using GroupService ---
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == joinButtonType) {
                MonthOption selectedMonthOption = monthComboBox.getValue();
                PaymentOption selectedPaymentOption = paymentComboBox.getValue();
                Integer selectedTurnOrder = turnOrderComboBox.getValue();

                if (selectedMonthOption == null || selectedPaymentOption == null || selectedTurnOrder == null) {
                    showAlert("Error", "Please select all options (months, payment, and turn order)", Alert.AlertType.ERROR);
                    return null;
                }

                try {
                    // Use GroupService for all group/participant logic
                    PaymentPlan paymentPlan = new PaymentPlan();
                    paymentPlan.setMonthsCount(selectedMonthOption.getMonthsCount());
                    paymentPlan.setMonthlyPayment(java.math.BigDecimal.valueOf(selectedPaymentOption.getMonthlyPayment()));

                    // Find or create group
                    List<Group> matchingGroups = groupService.findAvailableGroupsByPaymentPlan(paymentPlan, GroupStatus.WAITING_FOR_MEMBERS);
                    Group selectedGroup;
                    if (matchingGroups.isEmpty()) {
                        selectedGroup = groupService.createGroup(paymentPlan, paymentPlan.getMonthlyPayment(), paymentPlan.getMonthsCount());
                    } else {
                        selectedGroup = matchingGroups.get(0);
                        // Check if the selected turn order is available
                        try (org.hibernate.Session session = sessionFactory.openSession()) {
                            List<Participant> existingParticipants = participantService.getByGroup( selectedGroup);
                            boolean turnOrderTaken = existingParticipants.stream()
                                .anyMatch(p -> p.getTurnOrder() == selectedTurnOrder);
                            if (turnOrderTaken) {
                                showAlert("Error", "Selected turn order is already taken. Please choose another one.", Alert.AlertType.ERROR);
                                return null;
                            }
                        }
                    }

                    // Join group
                    groupService.joinGroup(currentUser, selectedGroup, selectedTurnOrder);

                    // Check if this was the last member to join
                    try (org.hibernate.Session session = sessionFactory.openSession()) {
                        List<Participant> allParticipants = participantService.getByGroup( selectedGroup);
                        if (allParticipants.size() == selectedGroup.getMaxMembers()) {
                            selectedGroup.setStatus(GroupStatus.PENDING_APPROVAL);
                            groupService.updateGroup(selectedGroup);
                            showAlert("Success", "Successfully joined group: " + selectedGroup.getGroupName() +
                                "\nYour turn order: " + selectedTurnOrder +
                                "\nGroup is now complete and pending approval!", Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Success", "Successfully joined group: " + selectedGroup.getGroupName() +
                                "\nYour turn order: " + selectedTurnOrder, Alert.AlertType.INFORMATION);
                        }
                    }

                    // Refresh all tables in the user dashboard
                    TabPane tabPane = (TabPane) primaryStage.getScene().lookup(".tab-pane");
                    if (tabPane != null) {
                        Tab userDashboardTab = tabPane.getTabs().get(0); // First tab is user dashboard
                        VBox dashboardContent = (VBox) userDashboardTab.getContent();
                        for (javafx.scene.Node node : dashboardContent.getChildren()) {
                            if (node instanceof TableView) {
                                @SuppressWarnings("unchecked")
                                TableView<Group> table = (TableView<Group>) node;
                                if (table.getColumns().get(0).getText().equals("Group Name")) {
                                    table.getItems().clear();
                                    try (org.hibernate.Session session = sessionFactory.openSession()) {
                                        table.getItems().addAll(groupService.getByMember(currentUser.getId()));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Failed to join group: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private Tab createUserProfileTab() {
        Tab tab = new Tab("My Profile");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create form fields
        TextField emailField = new TextField(currentUser.getEmail());
        TextField firstNameField = new TextField(currentUser.getFirstName());
        TextField lastNameField = new TextField(currentUser.getLastName());
        TextField mobileField = new TextField(currentUser.getMobileNumber());
        TextField bankAccountField = new TextField(currentUser.getBankAccount());
        TextField clearingNumberField = new TextField(currentUser.getClearingNumber());

        // Create labels and fields in a grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Add fields to grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(new Label(currentUser.getUsername()), 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Mobile Number:"), 0, 4);
        grid.add(mobileField, 1, 4);
        grid.add(new Label("Bank Account:"), 0, 5);
        grid.add(bankAccountField, 1, 5);
        grid.add(new Label("Clearing Number:"), 0, 6);
        grid.add(clearingNumberField, 1, 6);

        Label currentBalanceLabelText = new Label("Current Balance:");
        BigDecimal currentBalance = currentUser.getCurrentBalance();
        String stringBalance = currentBalance != null ? currentBalance.toString() + " SEK" : "0.00 SEK";
        Label currentBalanceValue = new Label(stringBalance); // Placeholder value
        grid.add(currentBalanceLabelText, 0, 7);
        grid.add(currentBalanceValue, 1, 7);
        // Create buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button editProfileBtn = new Button("Save Changes");
        Button changePasswordBtn = new Button("Change Password");
        Button refreshProfileBtn = new Button("Refresh");

        // Add edit profile functionality
        editProfileBtn.setOnAction(e -> {
            try {
                // Update user information
                currentUser.setEmail(emailField.getText());
                currentUser.setFirstName(firstNameField.getText());
                currentUser.setLastName(lastNameField.getText());
                currentUser.setMobileNumber(mobileField.getText());
                currentUser.setBankAccount(bankAccountField.getText());
                currentUser.setClearingNumber(clearingNumberField.getText());

                // Save changes to database
                userService.updateUser(currentUser);
                showAlert("Success", "Profile updated successfully", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Error", "Failed to update profile: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Add refresh profile functionality
        refreshProfileBtn.setOnAction(e -> {
            User refreshedUser = userService.getUserByUsername(currentUser.getUsername());
            if (refreshedUser != null) {
                currentUser = refreshedUser;
                emailField.setText(currentUser.getEmail());
                firstNameField.setText(currentUser.getFirstName());
                lastNameField.setText(currentUser.getLastName());
                mobileField.setText(currentUser.getMobileNumber());
                bankAccountField.setText(currentUser.getBankAccount());
                clearingNumberField.setText(currentUser.getClearingNumber());
                String refreshedBalance = currentUser.getCurrentBalance() != null ? currentUser.getCurrentBalance().toString() + " SEK" : "0.00 SEK";
                currentBalanceValue.setText(refreshedBalance);
            }
        });

        // Add change password functionality
        changePasswordBtn.setOnAction(e -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Change Password");
            dialog.setHeaderText("Enter new password");

            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            GridPane passwordGrid = new GridPane();
            passwordGrid.setHgap(10);
            passwordGrid.setVgap(10);
            passwordGrid.setPadding(new Insets(20));

            PasswordField currentPasswordField = new PasswordField();
            PasswordField newPasswordField = new PasswordField();
            PasswordField confirmPasswordField = new PasswordField();

            passwordGrid.add(new Label("Current Password:"), 0, 0);
            passwordGrid.add(currentPasswordField, 1, 0);
            passwordGrid.add(new Label("New Password:"), 0, 1);
            passwordGrid.add(newPasswordField, 1, 1);
            passwordGrid.add(new Label("Confirm New Password:"), 0, 2);
            passwordGrid.add(confirmPasswordField, 1, 2);

            dialog.getDialogPane().setContent(passwordGrid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    if (!currentPasswordField.getText().equals(currentUser.getPassword())) {
                        showAlert("Error", "Current password is incorrect", Alert.AlertType.ERROR);
                        return null;
                    }
                    if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                        showAlert("Error", "New passwords do not match", Alert.AlertType.ERROR);
                        return null;
                    }
                    return newPasswordField.getText();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newPassword -> {
                try {
                    currentUser.setPassword(newPassword);
                    userService.updateUser(currentUser);
                    showAlert("Success", "Password changed successfully", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to change password: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        buttonBox.getChildren().addAll(editProfileBtn, changePasswordBtn, refreshProfileBtn);

        // Add all components to the content
        content.getChildren().addAll(
            new Label("My Profile"),
            grid,
            buttonBox
        );

        tab.setContent(content);
        return tab;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAllPaymentsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("All Payments");
        dialog.setHeaderText("View All Payments");
        dialog.getDialogPane().setPrefSize(1200, 800);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create tab pane for current and future payments
        TabPane tabPane = new TabPane();
        
        // Create current payments tab
        Tab currentPaymentsTab = new Tab("Current Payments");
        TableView<Payment> currentPaymentsTable = createPaymentsTable();
        currentPaymentsTable.setPrefHeight(400); // Increased from default to 400
        currentPaymentsTab.setContent(currentPaymentsTable);
        
        // Create future payments tab
        Tab futurePaymentsTab = new Tab("Future Payments");
        TableView<Payment> futurePaymentsTable = createPaymentsTable();
        futurePaymentsTable.setPrefHeight(400); // Increased from default to 400
        futurePaymentsTab.setContent(futurePaymentsTable);
        
        tabPane.getTabs().addAll(currentPaymentsTab, futurePaymentsTab);

        // Load payments
        LocalDateTime now = LocalDateTime.now();
        List<Payment> allPayments = paymentService.getAllPayments();
            
        // Split payments into current and future
        List<Payment> currentPayments = allPayments.stream()
            .filter(p -> p.getDueDate().isBefore(now) || p.getDueDate().isEqual(now))
            .collect(Collectors.toList());
            
        List<Payment> futurePayments = allPayments.stream()
            .filter(p -> p.getDueDate().isAfter(now))
            .collect(Collectors.toList());

        currentPaymentsTable.getItems().addAll(currentPayments);
        futurePaymentsTable.getItems().addAll(futurePayments);

        // Add refresh button
        Button refreshPaymentBtn = new Button("Refresh");
        refreshPaymentBtn.setOnAction(e -> {
            currentPaymentsTable.getItems().clear();
            futurePaymentsTable.getItems().clear();
            
            List<Payment> refreshedPayments = paymentService.getAllPayments();
            
            // Update both tables
            currentPaymentsTable.getItems().addAll(
                refreshedPayments.stream()
                    .filter(p -> p.getDueDate().isBefore(now) || p.getDueDate().isEqual(now))
                    .collect(Collectors.toList())
            );
            
            futurePaymentsTable.getItems().addAll(
                refreshedPayments.stream()
                    .filter(p -> p.getDueDate().isAfter(now))
                    .collect(Collectors.toList())
            );
        });

        content.getChildren().addAll(tabPane, refreshPaymentBtn);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    private TableView<Payment> createPaymentsTable() {
        TableView<Payment> table = new TableView<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Group Name column
        TableColumn<Payment, String> groupNameCol = new TableColumn<>("Group Name");
        groupNameCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            if (payment != null && payment.getGroup() != null) {
                return new SimpleStringProperty(payment.getGroup().getGroupName());
            }
            return new SimpleStringProperty(""); // Or "N/A" or some other placeholder
        });
        groupNameCol.setPrefWidth(200);

        // Amount column
        TableColumn<Payment, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            if (payment != null && payment.getAmount() != null) {
                return new SimpleStringProperty(payment.getAmount().toString() + " SEK");
            }
            return new SimpleStringProperty("");
        });
        amountCol.setPrefWidth(100);
        // Due Date column
        TableColumn<Payment, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> {
            LocalDateTime dueDate = cellData.getValue().getDueDate();
            return new SimpleStringProperty(dueDate != null ? dueDate.format(formatter) : "");
        });
        
        // Paid At column
        TableColumn<Payment, String> paidAtCol = new TableColumn<>("Paid At");
        paidAtCol.setCellValueFactory(cellData -> {
            LocalDateTime paidAt = cellData.getValue().getPaidAt();
            return new SimpleStringProperty(paidAt != null ? paidAt.format(formatter) : "Not Paid");
        });

        // Status column
        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            if (payment != null && payment.getStatus() != null) {
                return new SimpleStringProperty(payment.getStatus().toString());
            }
            return new SimpleStringProperty("");
        });
        statusCol.setPrefWidth(100);
        
        // Add columns to table
        table.getColumns().addAll(groupNameCol, dueDateCol, paidAtCol, amountCol, statusCol);
        return table;
    }

    private Boolean isBalanceEnough(Payment payment) {
        if (payment.getAmount().compareTo(currentUser.getCurrentBalance()) > 0) {
            showAlert("Error", "Insufficient balance", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void handlePayment(Payment payment) {
        // Check if payment is already paid
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            showAlert("Error", "This payment has already been paid", Alert.AlertType.ERROR);
            return;
        }

        if (!isBalanceEnough(payment)) {
            showAlert("Error", "There is not enough balance to pay this payment", Alert.AlertType.ERROR);

        }

        // Create confirmation dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm Payment");
        dialog.setHeaderText("Confirm Payment Details");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        content.getChildren().addAll(
            new Label("Group: " + payment.getGroup().getGroupName()),
            new Label("Amount: " + payment.getAmount() + " SEK"),
            new Label("Due Date: " + payment.getDueDate())
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Update payment status
                    payment.setPaymentStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                    paymentService.updatePayment(payment);

                    // Deduct paid amount from current user's balance
                    BigDecimal newBalance = currentUser.getCurrentBalance().subtract(payment.getAmount());
                    currentUser.setCurrentBalance(newBalance);
                    userService.updateUser(currentUser);

                    // Update the round status if all payments for this round are paid
                    Round round = payment.getRound();
                    if (round != null) {
                        List<Payment> roundPayments = paymentService.getByRound(round);
                        boolean allPaid = roundPayments.stream()
                            .allMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);
                        if (allPaid) {
                            round.setStatus(RoundStatus.COMPLETED);
                            roundService.updateRound(round);

                            // Give the group total to the winner
                            Participant winner = round.getWinnerParticipant();
                            if (winner != null) {
                                User winnerUser = winner.getUser();
                                BigDecimal groupTotal = round.getGroup().getTotalAmount();
                                winnerUser.setCurrentBalance(winnerUser.getCurrentBalance().add(groupTotal));
                                userService.updateUser(winnerUser);
                            }
                        }
                    }

                    showAlert("Success", "Payment processed successfully", Alert.AlertType.INFORMATION);

                    // Only refresh the payments to make table
                    paymentsToMakeTable.getItems().clear();
                    List<Participant> userParticipants = participantService.getByUser(currentUser);
                    Set<Payment> userPayments = new HashSet<>();
                    for (Participant participant : userParticipants) {
                        userPayments.addAll(paymentService.getByGroupAndParticipant(participant.getGroup(), participant));
                    }
                    List<Payment> paymentsToMake = userPayments.stream()
                        .filter(p -> p.getCreator().getId().equals(currentUser.getId()))
                        .collect(Collectors.toList());
                    paymentsToMakeTable.getItems().addAll(paymentsToMake);
                    
                    // Update only the toPayLabel
                    BigDecimal totalToPay = paymentsToMake.stream()
                        .filter(p -> p.getPaymentStatus() != PaymentStatus.PAID)
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    toPayLabel.setText(totalToPay + " SEK");

                } catch (Exception e) {
                    showAlert("Error", "Failed to process payment: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private Tab createMonthPaymentManagementTab() {
        Tab tab = new Tab("Month & Payment Options");
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Create header
        Label headerLabel = new Label("Month & Payment Options Management");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create tab pane for month and payment options
        TabPane optionsTabPane = new TabPane();
        
        // Month Options Tab
        Tab monthTab = new Tab("Month Options");
        VBox monthContent = new VBox(10);
        monthContent.setPadding(new Insets(10));

        // Month Options Table
        TableView<MonthOption> monthTable = new TableView<>();
        monthTable.setPrefHeight(400); // Increased from default to 400
        TableColumn<MonthOption, Long> monthIdCol = new TableColumn<>("ID");
        monthIdCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        TableColumn<MonthOption, Integer> monthCountCol = new TableColumn<>("Months Count");
        monthCountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMonthsCount()).asObject());
        
        monthTable.getColumns().addAll(monthIdCol, monthCountCol);

        // Month Options Buttons
        HBox monthButtonBox = new HBox(10);
        Button addMonthBtn = new Button("Add Month Option");
        Button editMonthBtn = new Button("Edit Month Option");
        Button deleteMonthBtn = new Button("Delete Month Option");
        Button refreshMonthBtn = new Button("Refresh");
        
        monthButtonBox.getChildren().addAll(addMonthBtn, editMonthBtn, deleteMonthBtn, refreshMonthBtn);
        
        // Add Month Dialog
        addMonthBtn.setOnAction(e -> {
            Dialog<MonthOption> dialog = new Dialog<>();
            dialog.setTitle("Add Month Option");
            dialog.setHeaderText("Enter number of months");

            TextField monthsField = new TextField();
            monthsField.setPromptText("Number of months");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(new VBox(10, new Label("Months Count:"), monthsField));

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        int months = Integer.parseInt(monthsField.getText());
                        MonthOption newOption = new MonthOption(months);
                        monthOptionService.save(newOption);
                        return newOption;
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(option -> refreshMonthTable(monthTable));
        });

        // Edit Month Dialog
        editMonthBtn.setOnAction(e -> {
            MonthOption selected = monthTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a month option to edit", Alert.AlertType.ERROR);
                return;
            }

            Dialog<MonthOption> dialog = new Dialog<>();
            dialog.setTitle("Edit Month Option");
            dialog.setHeaderText("Edit number of months");

            TextField monthsField = new TextField(String.valueOf(selected.getMonthsCount()));
            monthsField.setPromptText("Number of months");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(new VBox(10, new Label("Months Count:"), monthsField));

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        int months = Integer.parseInt(monthsField.getText());
                        selected.setMonthsCount(months);
                        monthOptionService.update(selected);
                        return selected;
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(option -> refreshMonthTable(monthTable));
        });

        // Delete Month
        deleteMonthBtn.setOnAction(e -> {
            MonthOption selected = monthTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a month option to delete", Alert.AlertType.ERROR);
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Month Option");
            alert.setContentText("Are you sure you want to delete this month option?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    monthOptionService.deleteById(selected.getId());
                    refreshMonthTable(monthTable);
                }
            });
        });

        // Refresh Month Table
        refreshMonthBtn.setOnAction(e -> refreshMonthTable(monthTable));

        monthContent.getChildren().addAll(monthButtonBox, monthTable);
        monthTab.setContent(monthContent);

        // Payment Options Tab
        Tab paymentTab = new Tab("Payment Options");
        VBox paymentContent = new VBox(10);
        paymentContent.setPadding(new Insets(10));

        // Payment Options Table
        TableView<PaymentOption> paymentTable = new TableView<>();
        paymentTable.setPrefHeight(400); // Increased from default to 400
        TableColumn<PaymentOption, Long> paymentIdCol = new TableColumn<>("ID");
        paymentIdCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        TableColumn<PaymentOption, Integer> paymentAmountCol = new TableColumn<>("Monthly Payment (SEK)");
        paymentAmountCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMonthlyPayment()).asObject());
        
        paymentTable.getColumns().addAll(paymentIdCol, paymentAmountCol);

        // Payment Options Buttons
        HBox paymentButtonBox = new HBox(10);
        Button addPaymentBtn = new Button("Add Payment Option");
        Button editPaymentBtn = new Button("Edit Payment Option");
        Button deletePaymentBtn = new Button("Delete Payment Option");
        Button refreshPaymentBtn = new Button("Refresh");
        
        paymentButtonBox.getChildren().addAll(addPaymentBtn, editPaymentBtn, deletePaymentBtn, refreshPaymentBtn);

        // Add Payment Dialog
        addPaymentBtn.setOnAction(e -> {
            Dialog<PaymentOption> dialog = new Dialog<>();
            dialog.setTitle("Add Payment Option");
            dialog.setHeaderText("Enter monthly payment amount");

            TextField amountField = new TextField();
            amountField.setPromptText("Monthly payment amount (SEK)");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(new VBox(10, new Label("Monthly Payment (SEK):"), amountField));

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        int amount = Integer.parseInt(amountField.getText());
                        PaymentOption newOption = new PaymentOption(amount);
                        paymentOptionService.save(newOption);
                        return newOption;
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(option -> refreshPaymentTable(paymentTable));
        });

        // Edit Payment Dialog
        editPaymentBtn.setOnAction(e -> {
            PaymentOption selected = paymentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a payment option to edit", Alert.AlertType.ERROR);
                return;
            }

            Dialog<PaymentOption> dialog = new Dialog<>();
            dialog.setTitle("Edit Payment Option");
            dialog.setHeaderText("Edit monthly payment amount");

            TextField amountField = new TextField(String.valueOf(selected.getMonthlyPayment()));
            amountField.setPromptText("Monthly payment amount (SEK)");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(new VBox(10, new Label("Monthly Payment (SEK):"), amountField));

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        int amount = Integer.parseInt(amountField.getText());
                        selected.setMonthlyPayment(amount);
                        paymentOptionService.update(selected);
                        return selected;
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(option -> refreshPaymentTable(paymentTable));
        });

        // Delete Payment
        deletePaymentBtn.setOnAction(e -> {
            PaymentOption selected = paymentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a payment option to delete", Alert.AlertType.ERROR);
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Payment Option");
            alert.setContentText("Are you sure you want to delete this payment option?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    paymentOptionService.deleteById(selected.getId());
                    refreshPaymentTable(paymentTable);
                }
            });
        });

        // Refresh Payment Table
        refreshPaymentBtn.setOnAction(e -> refreshPaymentTable(paymentTable));

        paymentContent.getChildren().addAll(paymentButtonBox, paymentTable);
        paymentTab.setContent(paymentContent);

        optionsTabPane.getTabs().addAll(monthTab, paymentTab);
        content.getChildren().addAll(headerLabel, optionsTabPane);
        tab.setContent(content);

        // Initial data load
        refreshMonthTable(monthTable);
        refreshPaymentTable(paymentTable);

        return tab;
    }

    private void refreshMonthTable(TableView<MonthOption> table) {
        table.getItems().clear();
        table.getItems().addAll(monthOptionService.getAll());
    }

    private void refreshPaymentTable(TableView<PaymentOption> table) {
        table.getItems().clear();
        table.getItems().addAll(paymentOptionService.getAllMonthlyPayments());
    }

    public static void main(String[] args) {
        launch(args);
    }
} 