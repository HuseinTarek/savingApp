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

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.Scanner;
import java.util.Optional;

public class SavingsApplication extends Application {
    private final SessionFactory sessionFactory = SingletonSessionFactory.getSessionFactory();
    private User currentUser;
    private Stage primaryStage;

    // Controllers
    private final MonthlyPaymentController monthlyPaymentController;
    private final PaymentController paymentController;
    private final RoundController roundController;
    private final GroupController groupController;
    private final UserController userController;
    private final ParticipantController participantController;
    private final AdminMonthPaymentController adminMonthPaymentController;

    // Repositories
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final PaymentRepository paymentRepository;
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final RoundRepository roundRepository;
    private final PaymentPlanRepository paymentPlanRepository;
    private final MonthOptionRepository monthOptionRepository;
    private final PaymentOptionRepository paymentOptionRepository;
    private final DemoDataGenerator demoDataGenerator;

    public SavingsApplication() {
        // Initialize repositories
        this.userRepository = new UserRepository();
        this.groupRepository = new GroupRepository();
        this.participantRepository = new ParticipantRepository();
        this.paymentRepository = new PaymentRepository();
        this.monthlyPaymentRepository = new MonthlyPaymentRepository();
        this.roundRepository = new RoundRepository();
        this.paymentPlanRepository = new PaymentPlanRepository();
        this.monthOptionRepository = new MonthOptionRepository();
        this.paymentOptionRepository = new PaymentOptionRepository();
        
        // Initialize controllers
        this.participantController = new ParticipantController(participantRepository);
        this.monthlyPaymentController = new MonthlyPaymentController(monthlyPaymentRepository);
        this.paymentController = new PaymentController(paymentRepository, participantRepository, groupRepository);
        this.roundController = new RoundController(participantController, groupRepository, participantRepository, roundRepository);
        this.groupController = new GroupController(participantRepository, groupRepository, userRepository, roundRepository, paymentRepository, paymentPlanRepository);
        this.userController = new UserController(userRepository);
        this.adminMonthPaymentController = new AdminMonthPaymentController();

        // Initialize DemoDataGenerator
        this.demoDataGenerator = new DemoDataGenerator(roundRepository, paymentRepository, paymentPlanRepository, 
            monthOptionRepository, paymentOptionRepository);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("SavingsApplication start method reached.");
        this.primaryStage = primaryStage;
        
        // Clear all existing data
        paymentRepository.deleteAll();
        participantRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
        paymentPlanRepository.deleteAll();
        monthOptionRepository.deleteAll();
        paymentOptionRepository.deleteAll();
        
        // Generate basic demo data (users, payment plans, options)
        demoDataGenerator.generateAllDemoData();
        System.out.println("Demo data generation finished.");

        // Use application logic to match users to groups and create remaining data
        groupController.matchAndDistribute();
        
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

        ComboBox<Role> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(Role.USER, Role.ADMIN);  // Only show USER and ADMIN roles
        roleComboBox.setPromptText("Select Role");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);

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

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), roleComboBox.getValue()));
        registerButton.setOnAction(e -> showRegistrationScreen(roleComboBox.getValue()));

        formBox.getChildren().addAll(
            new Label("Login to Your Account"),
            roleComboBox,
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

    private void handleLogin(String username, String password, Role role) {
        if (role == null) {
            showAlert("Error", "Please select a role", Alert.AlertType.ERROR);
            return;
        }

        User user = userRepository.getByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getRole() == role) {
            currentUser = user;
            showMainMenu();
        } else {
            showAlert("Error", "Invalid credentials or role mismatch", Alert.AlertType.ERROR);
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
                userRepository.save(newUser);
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
                    createGroupManagementTab(),
                    createPaymentManagementTab(),
                    createUserManagementTab(),
                    createMonthPaymentOptionsTab(),
                    createRoundTab()
                );
            }
            case USER -> {
                tabPane.getTabs().addAll(
                    createUserGroupsTab(),
                    createUserPaymentsTab(),
                    createUserProfileTab()
                );
            }
        }

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginScreen());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("vbox");
        root.setStyle("-fx-background-color: #1976d2;"); // Solid blue background

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

    private Tab createGroupManagementTab() {
        Tab tab = new Tab("Group Management");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create a header section
        Label headerLabel = new Label("Group Management");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create buttons section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
        
        Button showActiveGroupsBtn = new Button("Show Active Groups");
        Button activatePendingGroupsBtn = new Button("Activate Pending Groups");
        Button viewAllGroupsBtn = new Button("View All Groups");
        Button clearGroupsBtn = new Button("Clear Table");

        // Set fixed width for all buttons
        double buttonWidth = 150;
        showActiveGroupsBtn.setPrefWidth(buttonWidth);
        activatePendingGroupsBtn.setPrefWidth(buttonWidth);
        viewAllGroupsBtn.setPrefWidth(buttonWidth);
        clearGroupsBtn.setPrefWidth(buttonWidth);

        buttonBox.getChildren().addAll(showActiveGroupsBtn, activatePendingGroupsBtn, viewAllGroupsBtn, clearGroupsBtn);

        // Create table section
        Label tableLabel = new Label("Select a group from the table below to manage its status:");
        TableView<Group> groupTable = new TableView<>();
        TableColumn<Group, String> groupNameCol = new TableColumn<>("Group Name");
        TableColumn<Group, String> statusCol = new TableColumn<>("Status");
        TableColumn<Group, Integer> memberCountCol = new TableColumn<>("Members");
        TableColumn<Group, String> creatorCol = new TableColumn<>("Creator");
        
        // Set up cell value factories
        groupNameCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(group.getGroupName());
        });
        
        statusCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(group.getStatus().toString());
        });
        
        memberCountCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            int count = participantRepository.getByGroup(group).size();
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });

        creatorCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(group.getCreator().getUsername());
        });

        groupTable.getColumns().addAll(groupNameCol, statusCol, memberCountCol, creatorCol);

        // Create action buttons section
        HBox actionBox = new HBox(10);
        Button manageGroupStatusBtn = new Button("Manage Selected Group Status");
        manageGroupStatusBtn.setDisable(true); // Initially disabled until a group is selected
        actionBox.getChildren().add(manageGroupStatusBtn);

        // Add button handlers
        showActiveGroupsBtn.setOnAction(e -> {
            try {
                List<Group> activeGroups = groupRepository.getActiveGroups();
                groupTable.getItems().clear();
                groupTable.getItems().addAll(activeGroups);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load active groups: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        activatePendingGroupsBtn.setOnAction(e -> {
            try {
                List<Group> pendingGroups = groupRepository.getPendingApprovalGroups();
                if (pendingGroups.isEmpty()) {
                    showAlert("No Groups to Activate", 
                        "There are currently no groups eligible for activation. Groups become eligible when they have reached their maximum number of members.", 
                        Alert.AlertType.INFORMATION);
                    return;
                }
                
                boolean anyActivated = false;
                StringBuilder incompleteGroups = new StringBuilder();
                
                for (Group group : pendingGroups) {
                    if (group.getParticipants().size() == group.getMaxMembers()) {
                        anyActivated = true;
                        
                        groupController.initializeGroupPayments(group);
                    } else {
                        incompleteGroups.append(String.format("\nGroup '%s': %d/%d members", 
                            group.getGroupName(), 
                            group.getParticipants().size(), 
                            group.getMaxMembers()));
                    }
                }
                
                if (anyActivated) {
                    showAlert("Success", "Successfully activated complete groups", Alert.AlertType.INFORMATION);
                    showActiveGroupsBtn.fire(); // Refresh the table
                }
                
                if (incompleteGroups.length() > 0) {
                    showAlert("Incomplete Groups", 
                        "The following groups cannot be activated because they don't have enough members:" + 
                        incompleteGroups.toString(), 
                        Alert.AlertType.WARNING);
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to activate groups: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        viewAllGroupsBtn.setOnAction(e -> {
            try {
                List<Group> allGroups = groupRepository.getAll();
                groupTable.getItems().clear();
                groupTable.getItems().addAll(allGroups);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load groups: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Enable/disable manage status button based on selection
        groupTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            manageGroupStatusBtn.setDisable(newSelection == null);
        });

        manageGroupStatusBtn.setOnAction(e -> {
            Group selectedGroup = groupTable.getSelectionModel().getSelectedItem();
            if (selectedGroup == null) {
                showAlert("Error", "Please select a group first", Alert.AlertType.ERROR);
                return;
            }

            Dialog<GroupStatus> dialog = new Dialog<>();
            dialog.setTitle("Change Group Status");
            dialog.setHeaderText("Select new status for group: " + selectedGroup.getGroupName());

            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            ComboBox<GroupStatus> statusComboBox = new ComboBox<>();
            // Only show relevant status options based on current status
            if (selectedGroup.getStatus() == GroupStatus.WAITING_FOR_MEMBERS) {
                statusComboBox.getItems().addAll(GroupStatus.WAITING_FOR_MEMBERS, GroupStatus.PENDING_APPROVAL);
            } else if (selectedGroup.getStatus() == GroupStatus.PENDING_APPROVAL) {
                statusComboBox.getItems().addAll(GroupStatus.PENDING_APPROVAL, GroupStatus.ACTIVE);
            } else if (selectedGroup.getStatus() == GroupStatus.ACTIVE) {
                statusComboBox.getItems().addAll(GroupStatus.ACTIVE, GroupStatus.COMPLETED);
            } else {
                statusComboBox.getItems().addAll(GroupStatus.values());
            }
            statusComboBox.setValue(selectedGroup.getStatus());

            dialog.getDialogPane().setContent(statusComboBox);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    GroupStatus newStatus = statusComboBox.getValue();
                    // Validate status change
                    if (selectedGroup.getStatus() == GroupStatus.WAITING_FOR_MEMBERS && 
                        newStatus == GroupStatus.PENDING_APPROVAL && 
                        selectedGroup.getParticipants().size() < selectedGroup.getMaxMembers()) {
                        showAlert("Error", "Cannot change status to PENDING_APPROVAL: Group needs " + 
                            selectedGroup.getMaxMembers() + " members but has only " + 
                            selectedGroup.getParticipants().size(), Alert.AlertType.ERROR);
                        return null;
                    }
                    return newStatus;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newStatus -> {
                try {
                    selectedGroup.setStatus(newStatus);
                    groupRepository.update(selectedGroup);
                    showAlert("Success", "Group status updated", Alert.AlertType.INFORMATION);
                    viewAllGroupsBtn.fire(); // Refresh the table
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update group status: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        // Add clear button handler
        clearGroupsBtn.setOnAction(e -> groupTable.getItems().clear());

        // Add all components to the content
        content.getChildren().addAll(
            headerLabel,
            buttonBox,
            tableLabel,
            groupTable,
            actionBox
        );

        tab.setContent(content);
        return tab;
    }

    private Tab createPaymentManagementTab() {
        Tab tab = new Tab("Payment Management");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create header
        Label headerLabel = new Label("Payment Management");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create buttons section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
        
        Button viewAllPaymentsBtn = new Button("View All Payments");
        Button viewMonthlyPaymentsBtn = new Button("View Monthly Payments");
        Button viewLatePaymentsBtn = new Button("View Late Payments");
        Button processPaymentBtn = new Button("Process Payment");
        Button clearPaymentsBtn = new Button("Clear Table");

        // Set fixed width for all buttons
        double buttonWidth = 150;
        viewAllPaymentsBtn.setPrefWidth(buttonWidth);
        viewMonthlyPaymentsBtn.setPrefWidth(buttonWidth);
        viewLatePaymentsBtn.setPrefWidth(buttonWidth);
        processPaymentBtn.setPrefWidth(buttonWidth);
        clearPaymentsBtn.setPrefWidth(buttonWidth);

        buttonBox.getChildren().addAll(viewAllPaymentsBtn, viewMonthlyPaymentsBtn, viewLatePaymentsBtn, processPaymentBtn, clearPaymentsBtn);

        // Create table
        TableView<Payment> paymentTable = new TableView<>();
        
        TableColumn<Payment, String> paymentIdCol = new TableColumn<>("Payment ID");
        paymentIdCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(payment.getId()));
        });

        TableColumn<Payment, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(payment.getAmount().toString());
        });

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(payment.getStatus().toString());
        });

        TableColumn<Payment, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(payment.getDueDate().toString());
        });

        TableColumn<Payment, String> participantCol = new TableColumn<>("Participant");
        participantCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(payment.getParticipant().getUser().getUsername());
        });

        paymentTable.getColumns().addAll(paymentIdCol, amountCol, statusCol, dueDateCol, participantCol);

        // Add button handlers
        viewAllPaymentsBtn.setOnAction(e -> {
            try {
                List<Payment> allPayments = paymentRepository.getAll();
                paymentTable.getItems().clear();
                paymentTable.getItems().addAll(allPayments);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load payments: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        viewMonthlyPaymentsBtn.setOnAction(e -> {
            try {
                List<Payment> monthlyPayments = paymentRepository.getMonthlyPayments();
                paymentTable.getItems().clear();
                paymentTable.getItems().addAll(monthlyPayments);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load monthly payments: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        viewLatePaymentsBtn.setOnAction(e -> {
            try {
                List<Payment> latePayments = paymentRepository.getLatePayments();
                paymentTable.getItems().clear();
                paymentTable.getItems().addAll(latePayments);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load late payments: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        processPaymentBtn.setOnAction(e -> {
            Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
            if (selectedPayment == null) {
                showAlert("Error", "Please select a payment to process", Alert.AlertType.ERROR);
                return;
            }

            Dialog<PaymentStatus> dialog = new Dialog<>();
            dialog.setTitle("Process Payment");
            dialog.setHeaderText("Update payment status for payment ID: " + selectedPayment.getId());

            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            ComboBox<PaymentStatus> statusComboBox = new ComboBox<>();
            statusComboBox.getItems().addAll(PaymentStatus.values());
            statusComboBox.setValue(selectedPayment.getStatus());

            dialog.getDialogPane().setContent(statusComboBox);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    return statusComboBox.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newStatus -> {
                try {
                    selectedPayment.setStatus(newStatus);
                    paymentRepository.update(selectedPayment);
                    showAlert("Success", "Payment status updated", Alert.AlertType.INFORMATION);
                    viewAllPaymentsBtn.fire(); // Refresh the table
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update payment status: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        // Add clear button handler
        clearPaymentsBtn.setOnAction(e -> paymentTable.getItems().clear());

        // Add all components to the content
        content.getChildren().addAll(
            headerLabel,
            buttonBox,
            paymentTable
        );

        tab.setContent(content);
        return tab;
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
                List<User> allUsers = userRepository.getAll();
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
                    userRepository.update(selectedUser);
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

    private Tab createMonthPaymentOptionsTab() {
        Tab tab = new Tab("Month & Payment Options");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create header
        Label headerLabel = new Label("Month & Payment Options Management");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create month options section
        VBox monthOptionsBox = new VBox(10);
        Label monthOptionsLabel = new Label("Month Options");
        monthOptionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<MonthOption> monthTable = new TableView<>();
        TableColumn<MonthOption, Long> monthIdCol = new TableColumn<>("ID");
        TableColumn<MonthOption, Integer> monthCountCol = new TableColumn<>("Months Count");
        
        monthIdCol.setCellValueFactory(cellData -> {
            MonthOption option = cellData.getValue();
            return new javafx.beans.property.SimpleLongProperty(option.getId()).asObject();
        });
        
        monthCountCol.setCellValueFactory(cellData -> {
            MonthOption option = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(option.getMonthsCount()).asObject();
        });

        monthTable.getColumns().addAll(monthIdCol, monthCountCol);

        HBox monthButtonsBox = new HBox(10);
        Button addMonthBtn = new Button("Add Month Option");
        Button editMonthBtn = new Button("Edit Month Option");
        Button deleteMonthBtn = new Button("Delete Month Option");
        monthButtonsBox.getChildren().addAll(addMonthBtn, editMonthBtn, deleteMonthBtn);

        // Create payment options section
        VBox paymentOptionsBox = new VBox(10);
        Label paymentOptionsLabel = new Label("Payment Options");
        paymentOptionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<PaymentOption> paymentTable = new TableView<>();
        TableColumn<PaymentOption, Long> paymentIdCol = new TableColumn<>("ID");
        TableColumn<PaymentOption, Integer> paymentAmountCol = new TableColumn<>("Monthly Payment (SEK)");
        
        paymentIdCol.setCellValueFactory(cellData -> {
            PaymentOption option = cellData.getValue();
            return new javafx.beans.property.SimpleLongProperty(option.getId()).asObject();
        });
        
        paymentAmountCol.setCellValueFactory(cellData -> {
            PaymentOption option = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(option.getMonthlyPayment()).asObject();
        });

        paymentTable.getColumns().addAll(paymentIdCol, paymentAmountCol);

        HBox paymentButtonsBox = new HBox(10);
        Button addPaymentBtn = new Button("Add Payment Option");
        Button editPaymentBtn = new Button("Edit Payment Option");
        Button deletePaymentBtn = new Button("Delete Payment Option");
        paymentButtonsBox.getChildren().addAll(addPaymentBtn, editPaymentBtn, deletePaymentBtn);

        // Add button handlers
        addMonthBtn.setOnAction(e -> {
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Add Month Option");
            dialog.setHeaderText("Enter number of months");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            TextField monthsField = new TextField();
            monthsField.setPromptText("Number of months");

            dialog.getDialogPane().setContent(monthsField);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        return Integer.parseInt(monthsField.getText());
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(months -> {
                try {
                    MonthOption newOption = new MonthOption(months);
                    monthOptionRepository.save(newOption);
                    refreshMonthOptions(monthTable);
                    showAlert("Success", "Month option added successfully", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to add month option: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        editMonthBtn.setOnAction(e -> {
            MonthOption selected = monthTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a month option to edit", Alert.AlertType.ERROR);
                return;
            }

            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Edit Month Option");
            dialog.setHeaderText("Enter new number of months");

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            TextField monthsField = new TextField(String.valueOf(selected.getMonthsCount()));
            monthsField.setPromptText("Number of months");

            dialog.getDialogPane().setContent(monthsField);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        return Integer.parseInt(monthsField.getText());
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(months -> {
                try {
                    selected.setMonthsCount(months);
                    monthOptionRepository.update(selected);
                    refreshMonthOptions(monthTable);
                    showAlert("Success", "Month option updated successfully", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update month option: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        deleteMonthBtn.setOnAction(e -> {
            MonthOption selected = monthTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a month option to delete", Alert.AlertType.ERROR);
                return;
            }

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Month Option");
            confirmDialog.setContentText("Are you sure you want to delete this month option?");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        monthOptionRepository.deleteById(selected.getId());
                        refreshMonthOptions(monthTable);
                        showAlert("Success", "Month option deleted successfully", Alert.AlertType.INFORMATION);
                    } catch (Exception ex) {
                        showAlert("Error", "Failed to delete month option: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        });

        addPaymentBtn.setOnAction(e -> {
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Add Payment Option");
            dialog.setHeaderText("Enter monthly payment amount in SEK");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            TextField amountField = new TextField();
            amountField.setPromptText("Monthly payment amount");

            dialog.getDialogPane().setContent(amountField);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        return Integer.parseInt(amountField.getText());
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(amount -> {
                try {
                    PaymentOption newOption = new PaymentOption(amount);
                    paymentOptionRepository.save(newOption);
                    refreshPaymentOptions(paymentTable);
                    showAlert("Success", "Payment option added successfully", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to add payment option: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        editPaymentBtn.setOnAction(e -> {
            PaymentOption selected = paymentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a payment option to edit", Alert.AlertType.ERROR);
                return;
            }

            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Edit Payment Option");
            dialog.setHeaderText("Enter new monthly payment amount in SEK");

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            TextField amountField = new TextField(String.valueOf(selected.getMonthlyPayment()));
            amountField.setPromptText("Monthly payment amount");

            dialog.getDialogPane().setContent(amountField);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        return Integer.parseInt(amountField.getText());
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(amount -> {
                try {
                    selected.setMonthlyPayment(amount);
                    paymentOptionRepository.update(selected);
                    refreshPaymentOptions(paymentTable);
                    showAlert("Success", "Payment option updated successfully", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update payment option: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        deletePaymentBtn.setOnAction(e -> {
            PaymentOption selected = paymentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Please select a payment option to delete", Alert.AlertType.ERROR);
                return;
            }

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Payment Option");
            confirmDialog.setContentText("Are you sure you want to delete this payment option?");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        paymentOptionRepository.deleteById(selected.getId());
                        refreshPaymentOptions(paymentTable);
                        showAlert("Success", "Payment option deleted successfully", Alert.AlertType.INFORMATION);
                    } catch (Exception ex) {
                        showAlert("Error", "Failed to delete payment option: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        });

        // Add refresh buttons
        Button refreshMonthBtn = new Button("Refresh Month Options");
        Button refreshPaymentBtn = new Button("Refresh Payment Options");
        
        refreshMonthBtn.setOnAction(e -> refreshMonthOptions(monthTable));
        refreshPaymentBtn.setOnAction(e -> refreshPaymentOptions(paymentTable));

        // Add all components to the content
        monthOptionsBox.getChildren().addAll(
            monthOptionsLabel,
            monthTable,
            monthButtonsBox,
            refreshMonthBtn
        );

        paymentOptionsBox.getChildren().addAll(
            paymentOptionsLabel,
            paymentTable,
            paymentButtonsBox,
            refreshPaymentBtn
        );

        content.getChildren().addAll(
            headerLabel,
            monthOptionsBox,
            paymentOptionsBox
        );

        // Initial load of data
        refreshMonthOptions(monthTable);
        refreshPaymentOptions(paymentTable);

        tab.setContent(content);
        return tab;
    }

    private void refreshMonthOptions(TableView<MonthOption> table) {
        try {
            List<MonthOption> options = monthOptionRepository.getAll();
            table.getItems().clear();
            table.getItems().addAll(options);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load month options: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshPaymentOptions(TableView<PaymentOption> table) {
        try {
            List<PaymentOption> options = paymentOptionRepository.getAllMonthlyPayments();
            table.getItems().clear();
            table.getItems().addAll(options);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load payment options: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Tab createUserGroupsTab() {
        Tab tab = new Tab("My Groups");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Button viewMyGroupsBtn = new Button("View My Groups");
        Button joinGroupBtn = new Button("Join Group");
        Button clearMyGroupsBtn = new Button("Clear Table");

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        buttonRow.getChildren().addAll(viewMyGroupsBtn, joinGroupBtn, clearMyGroupsBtn);

        // Main group table
        TableView<Group> groupTable = new TableView<>();
        TableColumn<Group, String> groupNameCol = new TableColumn<>("Group Name");
        TableColumn<Group, String> statusCol = new TableColumn<>("Status");
        TableColumn<Group, String> contributionCol = new TableColumn<>("Monthly Contribution");
        TableColumn<Group, String> membersCol = new TableColumn<>("Members");
        TableColumn<Group, String> currentMonthCol = new TableColumn<>("Current Month");
        groupTable.getColumns().addAll(groupNameCol, statusCol, contributionCol, membersCol, currentMonthCol);

        // Set up cell value factories
        groupNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroupName()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        contributionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonthlyContribution().toString()));
        membersCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            return new SimpleStringProperty(group.getParticipants().size() + "/" + group.getMaxMembers());
        });
        currentMonthCol.setCellValueFactory(cellData -> {
            Group group = cellData.getValue();
            if (group.getStatus() == GroupStatus.ACTIVE) {
                long monthsSinceStart = java.time.temporal.ChronoUnit.MONTHS.between(
                    group.getStartDate(), LocalDateTime.now());
                return new SimpleStringProperty(String.valueOf(monthsSinceStart + 1));
            }
            return new SimpleStringProperty("N/A");
        });

        // Add button handlers
        viewMyGroupsBtn.setOnAction(e -> {
            try {
                List<Group> userGroups = groupRepository.getByMember(currentUser.getId());
                groupTable.getItems().clear();
                groupTable.getItems().addAll(userGroups);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load groups: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        joinGroupBtn.setOnAction(e -> {
            try {
                // Get all available month options
                List<MonthOption> monthOptions = monthOptionRepository.getAll();
                if (monthOptions.isEmpty()) {
                    showAlert("Info", "No month options available. Please contact an administrator.", Alert.AlertType.INFORMATION);
                    return;
                }

                // Get all available payment options
                List<PaymentOption> paymentOptions = paymentOptionRepository.getAllMonthlyPayments();
                if (paymentOptions.isEmpty()) {
                    showAlert("Info", "No payment options available. Please contact an administrator.", Alert.AlertType.INFORMATION);
                    return;
                }

                // Create dialog for month and payment selection
                Dialog<Pair<MonthOption, PaymentOption>> dialog = new Dialog<>();
                dialog.setTitle("Join Group");
                dialog.setHeaderText("Select Month and Payment Options");

                ButtonType joinButtonType = new ButtonType("Join", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(joinButtonType, ButtonType.CANCEL);

                // Create month options table
                TableView<MonthOption> monthTable = new TableView<>();
                TableColumn<MonthOption, String> monthCountCol = new TableColumn<>("Months");
                monthCountCol.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(String.valueOf(cellData.getValue().getMonthsCount())));
                monthTable.getColumns().add(monthCountCol);
                monthTable.getItems().addAll(monthOptions);

                // Create payment options table
                TableView<PaymentOption> paymentTable = new TableView<>();
                TableColumn<PaymentOption, String> paymentAmountCol = new TableColumn<>("Monthly Payment (SEK)");
                paymentAmountCol.setCellValueFactory(cellData -> 
                    new SimpleStringProperty(String.valueOf(cellData.getValue().getMonthlyPayment())));
                paymentTable.getColumns().add(paymentAmountCol);
                paymentTable.getItems().addAll(paymentOptions);

                // Create horizontal layout for tables
                HBox tablesBox = new HBox(20); // 20 pixels spacing between tables
                tablesBox.setPadding(new Insets(10));
                
                // Create VBox containers for each table with their labels
                VBox monthBox = new VBox(10);
                monthBox.getChildren().addAll(
                    new Label("Select Number of Months:"),
                    monthTable
                );
                
                VBox paymentBox = new VBox(10);
                paymentBox.getChildren().addAll(
                    new Label("Select Monthly Payment:"),
                    paymentTable
                );
                
                // Add both boxes to the horizontal layout
                tablesBox.getChildren().addAll(monthBox, paymentBox);
                
                // Set the dialog content
                dialog.getDialogPane().setContent(tablesBox);

                // Convert result to Pair<MonthOption, PaymentOption>
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == joinButtonType) {
                        MonthOption selectedMonth = monthTable.getSelectionModel().getSelectedItem();
                        PaymentOption selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
                        if (selectedMonth != null && selectedPayment != null) {
                            return new Pair<>(selectedMonth, selectedPayment);
                        }
                    }
                    return null;
                });

                // Handle the result
                dialog.showAndWait().ifPresent(result -> {
                    try {
                        MonthOption selectedMonth = result.getKey();
                        PaymentOption selectedPayment = result.getValue();

                        // Find or create a matching group
                        PaymentPlan paymentPlan = new PaymentPlan();
                        paymentPlan.setMonthsCount(selectedMonth.getMonthsCount());
                        paymentPlan.setMonthlyPayment(new BigDecimal(selectedPayment.getMonthlyPayment()));
                        
                        // Save the payment plan first
                        paymentPlanRepository.save(paymentPlan);
                        
                        List<Group> matchingGroups = groupRepository.findAvailableGroupsByPaymentPlan(
                            paymentPlan,
                            GroupStatus.WAITING_FOR_MEMBERS
                        );

                        Group selectedGroup;
                        if (matchingGroups.isEmpty()) {
                            // Create new group
                            selectedGroup = new Group();
                            selectedGroup.setMonthlyContribution(new BigDecimal(selectedPayment.getMonthlyPayment()));
                            selectedGroup.setMaxMembers(selectedMonth.getMonthsCount());
                            selectedGroup.setStatus(GroupStatus.WAITING_FOR_MEMBERS);
                            selectedGroup.setPaymentBy(PaymentBy.USER_PAYMENT);
                            selectedGroup.setStartDate(java.time.LocalDateTime.now());
                            selectedGroup.setEndDate(java.time.LocalDateTime.now().plusMonths(selectedMonth.getMonthsCount()));
                            selectedGroup.setTotalAmount(new BigDecimal(selectedPayment.getMonthlyPayment()).multiply(new BigDecimal(selectedMonth.getMonthsCount())));
                            selectedGroup.setGroupName(selectedGroup.generateGroupName(selectedGroup.getStartDate(), selectedGroup.getEndDate(), selectedGroup.getTotalAmount()));
                            selectedGroup.setCreator(currentUser);
                            selectedGroup.setPaymentPlan(paymentPlan);
                            groupRepository.save(selectedGroup);
                        } else {
                            // Use existing group
                            selectedGroup = matchingGroups.get(0);
                        }
                        List<Participant> participants = selectedGroup.getParticipants();
                        List<Integer> takenTurnOrder = new ArrayList<>();
                        for (Participant p : participants) {
                            takenTurnOrder.add(p.getTurnOrder());
                        }
                        List<Integer> availableTurnOrders = new ArrayList<>();
                        for (int i = 1; i <= selectedGroup.getMaxMembers(); i++) {
                            if (!takenTurnOrder.contains(i)) {
                                availableTurnOrders.add(i);
                            }
                        }

                        // Show dialog for turn order selection
                        ChoiceDialog<Integer> turnOrderDialog = new ChoiceDialog<>(availableTurnOrders.get(0), availableTurnOrders);
                        turnOrderDialog.setTitle("Select Turn Order");
                        turnOrderDialog.setHeaderText("Available Turn Orders");
                        turnOrderDialog.setContentText("Choose your turn order:");

                        Optional<Integer> selectedTurnOrder = turnOrderDialog.showAndWait();
                        if (selectedTurnOrder.isEmpty()) {
                            showAlert("Error", "Turn order selection cancelled", Alert.AlertType.ERROR);
                            return;
                        }

                        int turnOrder = selectedTurnOrder.get();
                        
                        // Use GroupController to add user to group as participant
                        try {
                            groupController.addUserToGroupAsParticipant(selectedGroup, currentUser, selectedGroup.getPaymentPlan(), turnOrder);
                            showAlert("Success", "Successfully joined group: " + selectedGroup.getGroupName(), Alert.AlertType.INFORMATION);
                            viewMyGroupsBtn.fire(); // Refresh the groups table
                        } catch (Exception ex) {
                            showAlert("Error", "Failed to join group: " + ex.getMessage(), Alert.AlertType.ERROR);
                        }
                    } catch (Exception ex) {
                        showAlert("Error", "Failed to load options: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception ex) {
                showAlert("Error", "Failed to load options: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Add clear button handler
        clearMyGroupsBtn.setOnAction(e -> {
            groupTable.getItems().clear();
        });

        // Add all components to the content
        content.getChildren().addAll(
            buttonRow,
            new Label("My Groups"),
            groupTable
        );

        tab.setContent(content);
        return tab;
    }

    private Tab createUserPaymentsTab() {
        Tab tab = new Tab("My Payments");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Create buttons
        Button viewMyPaymentsBtn = new Button("View My Payments");
        Button viewFuturePaymentsBtn = new Button("View Future Payments");
        Button viewFutureReceivingBtn = new Button("View Future Receiving");
        Button clearMyPaymentsBtn = new Button("Clear Table");

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        buttonRow.getChildren().addAll(viewMyPaymentsBtn, viewFuturePaymentsBtn, viewFutureReceivingBtn, clearMyPaymentsBtn);

        // Create table
        TableView<Payment> paymentTable = new TableView<>();
        
        // Add columns
        TableColumn<Payment, String> groupNameCol = new TableColumn<>("Group");
        groupNameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGroup().getGroupName()));
        
        TableColumn<Payment, String> amountCol = new TableColumn<>("Amount (SEK)");
        amountCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAmount().toString()));
        
        TableColumn<Payment, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDueDate().toString()));
        
        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        TableColumn<Payment, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            return new SimpleStringProperty(payment.getPaymentBy().toString());
        });

        paymentTable.getColumns().addAll(groupNameCol, amountCol, dueDateCol, statusCol, typeCol);

        // Add button handlers
        viewMyPaymentsBtn.setOnAction(e -> {
            try {
                // Get all groups where the user is a participant
                List<Group> userGroups = groupRepository.getByMember(currentUser.getId());
                List<Payment> allPayments = new ArrayList<>();
                
                for (Group group : userGroups) {
                    // Get the participant for this user in this group
                    Participant participant = participantRepository.getByUserAndGroup(currentUser, group);
                    if (participant != null) {
                        // Get all payments for this participant
                        List<Payment> participantPayments = paymentRepository.getByParticipant(participant.getId());
                        allPayments.addAll(participantPayments);
                    }
                }
                
                paymentTable.getItems().clear();
                paymentTable.getItems().addAll(allPayments);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load payments: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

            viewFuturePaymentsBtn.setOnAction(e -> {
                try {
                    // Get all groups where the user is a participant
                    List<Group> userGroups = groupRepository.getByMember(currentUser.getId());
                    List<Payment> futurePayments = new ArrayList<>();
                    
                    for (Group group : userGroups) {
                    //    if (group.getStatus() == GroupStatus.PENDING_APPROVAL) {
                            System.out.println("Group ID: " + group.getId() + ", Status: " + group.getStatus());

                            // Get the participant for this user in this group
                            Participant participant = participantRepository.getByUserAndGroup(currentUser, group);
                            if (participant != null) {
                                // Get all future rounds for this group
                                List<Round> futureRounds = roundRepository.getAllRoundsByGroupId(group.getId());
                                for (Round round : futureRounds) {
                                    // Get the actual payment record for this round
                                    Payment payment = paymentRepository.getByParticipantAndRound(participant.getId(), round.getId());
                                    if (payment != null) {
                                        futurePayments.add(payment);
                                    }
                                }
                //            }
                        }
                }
                System.out.println("Payments found: " + futurePayments.size());

                
                paymentTable.getItems().clear();
                paymentTable.getItems().addAll(futurePayments);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load future payments: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        viewFutureReceivingBtn.setOnAction(e -> {
            try {
                // Get all groups where the user is a participant
                List<Group> userGroups = groupRepository.getByMember(currentUser.getId());
                List<Payment> futureReceiving = new ArrayList<>();
                
                for (Group group : userGroups) {
                    if (group.getStatus() == GroupStatus.ACTIVE) {
                        // Get the participant for this user in this group
                        Participant participant = participantRepository.getByUserAndGroup(currentUser, group);
                        if (participant != null) {
                            // Get all future rounds where this participant will receive money
                            List<Round> futureRounds = roundRepository.getFutureRoundsByGroup(group.getId());
                            for (Round round : futureRounds) {
                                if (round.getWinnerParticipant() != null && 
                                    round.getWinnerParticipant().getId().equals(participant.getId())) {
                                    Payment payment = new Payment();
                                    payment.setGroup(group);
                                    payment.setRound(round);
                                    payment.setAmount(group.getMonthlyContribution().multiply(
                                        new BigDecimal(group.getMaxMembers())));
                                    payment.setDueDate(round.getEndDate());
                                    payment.setStatus(PaymentStatus.PENDING);
                                    payment.setPaymentBy(PaymentBy.SYSTEM_PAYMENT);
                                    futureReceiving.add(payment);
                                }
                            }
                        }
                    }
                }
                
                paymentTable.getItems().clear();
                paymentTable.getItems().addAll(futureReceiving);
            } catch (Exception ex) {
                showAlert("Error", "Failed to load future receiving amounts: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Add clear button handler
        clearMyPaymentsBtn.setOnAction(e -> paymentTable.getItems().clear());

        // Add all components to the content
        content.getChildren().addAll(
            new Label("My Payments and Receiving"),
            buttonRow,
            paymentTable
        );

        tab.setContent(content);
        return tab;
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

        // Create buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button editProfileBtn = new Button("Save Changes");
        Button changePasswordBtn = new Button("Change Password");

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
                userRepository.update(currentUser);
                showAlert("Success", "Profile updated successfully", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Error", "Failed to update profile: " + ex.getMessage(), Alert.AlertType.ERROR);
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
                    userRepository.update(currentUser);
                    showAlert("Success", "Password changed successfully", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to change password: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        buttonBox.getChildren().addAll(editProfileBtn, changePasswordBtn);

        // Add all components to the content
        content.getChildren().addAll(
            new Label("My Profile"),
            grid,
            buttonBox
        );

        tab.setContent(content);
        return tab;
    }

    private Tab createRoundTab() {
        Tab tab = new Tab("Round Management");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Create table for rounds
        TableView<Round> roundTable = new TableView<>();
        
        // Add columns
        TableColumn<Round, String> groupNameCol = new TableColumn<>("Group");
        groupNameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getGroup().getName()));
        
        TableColumn<Round, String> roundNumberCol = new TableColumn<>("Round #");
        roundNumberCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getRoundNumber())));
        
        TableColumn<Round, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAmount().toString()));
        
        TableColumn<Round, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        TableColumn<Round, String> winnerCol = new TableColumn<>("Winner");
        winnerCol.setCellValueFactory(cellData -> {
            Participant winner = cellData.getValue().getWinnerParticipant();
            return new SimpleStringProperty(winner != null ? winner.getUser().getUsername() : "Not Selected");
        });

        roundTable.getColumns().addAll(groupNameCol, roundNumberCol, amountCol, statusCol, winnerCol);

        // Create buttons
        HBox buttonBox = new HBox(10);
        Button viewActiveRoundsBtn = new Button("View Active Rounds");
        Button viewCompletedRoundsBtn = new Button("View Completed Rounds");
        Button completeRoundBtn = new Button("Complete Round");

        buttonBox.getChildren().addAll(viewActiveRoundsBtn, viewCompletedRoundsBtn, completeRoundBtn);

        // Add button handlers
        viewActiveRoundsBtn.setOnAction(e -> {
            List<Round> activeRounds = roundRepository.getByStatus(RoundStatus.ACTIVE);
            roundTable.getItems().setAll(activeRounds);
        });

        viewCompletedRoundsBtn.setOnAction(e -> {
            List<Round> completedRounds = roundRepository.getByStatus(RoundStatus.COMPLETED);
            roundTable.getItems().setAll(completedRounds);
        });

        completeRoundBtn.setOnAction(e -> {
            Round selectedRound = roundTable.getSelectionModel().getSelectedItem();
            if (selectedRound == null) {
                showAlert("Error", "Please select a round first", Alert.AlertType.ERROR);
                return;
            }

            if (selectedRound.getStatus() != RoundStatus.ACTIVE) {
                showAlert("Error", "Can only complete active rounds", Alert.AlertType.ERROR);
                return;
            }

            if (selectedRound.getWinnerParticipant() == null) {
                showAlert("Error", "Round must have a winner before completing", Alert.AlertType.ERROR);
                return;
            }

            try {
                selectedRound.setStatus(RoundStatus.COMPLETED);
                roundRepository.update(selectedRound);
                roundTable.refresh();
                showAlert("Success", "Round completed successfully", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Error", "Failed to complete round: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        content.getChildren().addAll(buttonBox, roundTable);
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

    private void checkGroupStatus(Group group) {
        if (group.getParticipants().size() == group.getMaxMembers()) {
            group.setStatus(GroupStatus.PENDING_APPROVAL);
            groupRepository.update(group);
            showAlert("Group Status", "Group is now pending approval with " + 
                group.getParticipants().size() + "/" + group.getMaxMembers() + " members", Alert.AlertType.INFORMATION);
        }
    }

    private void joinGroup(Group selectedGroup) {
        if (selectedGroup.getStatus() == GroupStatus.WAITING_FOR_MEMBERS && 
            selectedGroup.getParticipants().size() < selectedGroup.getMaxMembers()) {
            // ... existing join group code ...
        } else {
            showAlert("Cannot Join Group", 
                "This group requires " + selectedGroup.getMaxMembers() + " members but has only " +
                selectedGroup.getParticipants().size() + " members.", Alert.AlertType.ERROR);
        }
    }

    private javafx.beans.property.StringProperty createMemberCountProperty(Group group) {
        return new SimpleStringProperty(group.getParticipants().size() + "/" + group.getMaxMembers());
    }

    private void createNewGroup(MonthOption selectedMonth, PaymentOption selectedPayment) {
        Group selectedGroup = new Group();
        selectedGroup.setMonthlyContribution(new BigDecimal(selectedPayment.getMonthlyPayment()));
        selectedGroup.setMaxMembers(selectedMonth.getMonthsCount());
        // ... rest of createNewGroup code ...

        // Create rounds for the group
        for (int i = 1; i <= selectedGroup.getMaxMembers(); i++) {
            // ... round creation code ...
        }
    }

    private void checkGroupFull(Group selectedGroup) {
        if (selectedGroup.getParticipants().size() == selectedGroup.getMaxMembers()) {
            selectedGroup.setStatus(GroupStatus.PENDING_APPROVAL);
            groupRepository.update(selectedGroup);
            showAlert("Group Status", "Group is now pending approval!", Alert.AlertType.INFORMATION);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 