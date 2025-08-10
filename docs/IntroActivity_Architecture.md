# IntroActivity Architecture Documentation

## 🏗️ Overview

The `IntroActivity` is a critical component in the LifeCycle app that implements a permission-gated onboarding flow using MVP (Model-View-Presenter) architecture. It ensures users grant notification permissions before accessing the main application.

## 📊 Architecture Diagram

```mermaid
graph TB
    subgraph "IntroActivity Architecture"
        IA[IntroActivity]
        NPF[NotificationPermissionFragment]
        NPC[NotificationPermissionContract]
        NPP[NotificationPermissionPresenter]
        
        IA --> NPF
        NPF --> NPC
        NPF --> NPP
        NPP --> NPC
        
        IA -.-> MA[MainActivity]
        IA -.-> Settings[System Settings]
    end
    
    subgraph "External Dependencies"
        AppIntro[AppIntro2 Library]
        Android[Android System]
    end
    
    IA --> AppIntro
    NPF --> Android
    NPP --> Android
```

## 🔄 Component Relationships

```mermaid
classDiagram
    class IntroActivity {
        -isNavigationEnabled: boolean
        +onCreate()
        +onSkipPressed()
        +onNextPressed()
        +onDonePressed()
        +enableNavigation()
        +disableNavigation()
        +openNotificationSettings()
        -setupIntro()
        -shouldAllowNavigation()
        -createNotificationSettingsIntent()
        -goToMainActivity()
    }
    
    class NotificationPermissionFragment {
        -permissionButton: Button
        -permissionLauncher: ActivityResultLauncher
        -presenter: Presenter
        +onCreateView()
        +onResume()
        +onDestroy()
        +showPermissionGrantedUI()
        +showPermissionDeniedUI()
        +showSystemPermissionDialog()
        +showCustomPermissionDialog()
    }
    
    class NotificationPermissionContract {
        <<interface>>
        +View
        +Presenter
    }
    
    class NotificationPermissionPresenter {
        -context: Context
        -view: View
        -denialCount: int
        +onViewCreated()
        +onViewResumed()
        +onPermissionButtonClicked()
        +onSystemPermissionResult()
    }
    
    IntroActivity --> NotificationPermissionFragment
    NotificationPermissionFragment ..|> NotificationPermissionContract
    NotificationPermissionPresenter ..|> NotificationPermissionContract
    NotificationPermissionFragment --> NotificationPermissionPresenter
```

## 🚀 User Flow Diagram

```mermaid
flowchart TD
    Start([App Launch]) --> Splash[SplashActivity]
    Splash --> Intro[IntroActivity]
    
    Intro --> Setup[Setup: Add NotificationPermissionFragment]
    Setup --> ShowUI[Display Permission UI]
    
    ShowUI --> Check{Android 13+?}
    Check -->|No| Auto[Auto-grant Permission]
    Check -->|Yes| Button[Show Permission Button]
    
    Auto --> Enable[Enable Navigation]
    Enable --> Next[Next Button Active]
    
    Button --> Click[User Clicks Button]
    Click --> FirstRequest{First/Second Request?}
    
    FirstRequest -->|Yes| SystemDialog[System Permission Dialog]
    FirstRequest -->|No| CustomDialog[Custom Permission Dialog]
    
    SystemDialog --> Grant1{Permission Granted?}
    Grant1 -->|Yes| Success[✓ Permission Granted]
    Grant1 -->|No| Deny1[Increment Denial Count]
    
    CustomDialog --> Grant2{User Allows?}
    Grant2 -->|Yes| Settings[Open System Settings]
    Grant2 -->|No| Deny2[User Remains on Screen]
    
    Deny1 --> Button
    Deny2 --> Button
    
    Success --> Enable
    Settings --> Return[User Returns from Settings]
    Return --> CheckPerm{Permission Status?}
    CheckPerm -->|Granted| Success
    CheckPerm -->|Denied| Button
    
    Next --> Skip{Skip Pressed?}
    Skip -->|Yes| Settings
    Skip -->|No| NavCheck{Navigation Enabled?}
    
    NavCheck -->|Yes| Main[MainActivity]
    NavCheck -->|No| Block[Stay on Intro]
    
    Block --> Button
    Main --> End([App Running])
```

## 🎯 Permission Handling Strategy

```mermaid
stateDiagram-v2
    [*] --> Initial: onCreate()
    
    Initial --> CheckAndroid: Check Android Version
    
    CheckAndroid --> Android12: API < 33
    CheckAndroid --> Android13: API >= 33
    
    Android12 --> AutoGranted: Permission Auto-granted
    AutoGranted --> NavigationEnabled
    
    Android13 --> PermissionUI: Show Permission Button
    
    PermissionUI --> UserClick: Button Clicked
    
    UserClick --> FirstAttempt: denialCount < 2
    UserClick --> MultipleAttempts: denialCount >= 2
    
    FirstAttempt --> SystemDialog: Launch System Dialog
    MultipleAttempts --> CustomDialog: Show Custom Dialog
    
    SystemDialog --> SystemGranted: User Allows
    SystemDialog --> SystemDenied: User Denies
    
    CustomDialog --> SettingsRedirect: User Clicks Allow
    CustomDialog --> CustomDenied: User Clicks Don't Allow
    
    SystemGranted --> NavigationEnabled
    SystemDenied --> PermissionUI
    CustomDenied --> PermissionUI
    
    SettingsRedirect --> SettingsOpen: Launch System Settings
    SettingsOpen --> SettingsReturn: User Returns
    SettingsReturn --> CheckStatus: Check Permission Status
    
    CheckStatus --> NavigationEnabled: Permission Granted
    CheckStatus --> PermissionUI: Permission Still Denied
    
    NavigationEnabled --> Ready: Next Button Active
    Ready --> MainActivity: User Navigates Forward
    
    MainActivity --> [*]
```

## 🔧 Method Interaction Flow

```mermaid
sequenceDiagram
    participant User
    participant IntroActivity as IntroActivity
    participant Fragment as NotificationPermissionFragment
    participant Presenter as NotificationPermissionPresenter
    participant System as Android System
    
    User->>IntroActivity: App Launch
    IntroActivity->>IntroActivity: onCreate()
    IntroActivity->>IntroActivity: setupIntro()
    IntroActivity->>Fragment: addSlide()
    
    Fragment->>Fragment: onCreateView()
    Fragment->>Presenter: create presenter
    Fragment->>Presenter: onViewCreated()
    
    Fragment->>Fragment: onResume()
    Fragment->>Presenter: onViewResumed()
    Presenter->>Presenter: checkAndUpdatePermissionState()
    Presenter->>Fragment: showPermissionDeniedUI()
    
    User->>Fragment: Click Permission Button
    Fragment->>Presenter: onPermissionButtonClicked()
    
    alt First/Second Attempt
        Presenter->>Fragment: showSystemPermissionDialog()
        Fragment->>System: launch permission request
        System-->>Fragment: permission result
        Fragment->>Presenter: onSystemPermissionResult()
        
        alt Permission Granted
            Presenter->>Fragment: showPermissionGrantedUI()
            Presenter->>IntroActivity: enableNavigation()
        else Permission Denied
            Presenter->>Fragment: showPermissionDeniedUI()
            Presenter->>IntroActivity: disableNavigation()
        end
    else Multiple Attempts
        Presenter->>Fragment: showCustomPermissionDialog()
        
        alt User Clicks Allow
            Fragment->>Presenter: onCustomDialogAllowClicked()
            Presenter->>Fragment: openNotificationSettings()
            Fragment->>IntroActivity: openNotificationSettings()
            IntroActivity->>System: launch settings intent
        else User Clicks Don't Allow
            Fragment->>Presenter: onCustomDialogDenyClicked()
            Presenter->>Fragment: showPermissionDeniedUI()
        end
    end
    
    User->>IntroActivity: Click Next/Done
    IntroActivity->>IntroActivity: shouldAllowNavigation()
    
    alt Navigation Enabled
        IntroActivity->>IntroActivity: goToMainActivity()
        IntroActivity->>System: start MainActivity
    else Navigation Disabled
        IntroActivity->>IntroActivity: stay on current screen
    end
```

## 🏛️ MVP Architecture Benefits

### **Separation of Concerns**
```mermaid
graph LR
    subgraph "View Layer"
        V1[IntroActivity<br/>Navigation Logic]
        V2[NotificationPermissionFragment<br/>UI Components]
    end
    
    subgraph "Contract Layer"
        C[NotificationPermissionContract<br/>Interface Definitions]
    end
    
    subgraph "Presenter Layer"
        P[NotificationPermissionPresenter<br/>Business Logic]
    end
    
    V1 -.-> V2
    V2 --> C
    P --> C
    V2 <--> P
```

### **Key Architecture Advantages**
1. **Testability**: Presenter can be unit tested independently
2. **Maintainability**: Clear separation of UI and business logic
3. **Reusability**: Presenter logic can be reused with different views
4. **Scalability**: Easy to add new features without affecting existing code

## 🎮 User Experience States

```mermaid
stateDiagram-v2
    [*] --> Loading: App Starts
    Loading --> PermissionScreen: IntroActivity Loads
    
    state PermissionScreen {
        [*] --> ButtonVisible: Show Permission Button
        ButtonVisible --> ButtonClicked: User Interaction
        
        state ButtonClicked {
            [*] --> SystemDialog: First/Second Attempt
            [*] --> CustomDialog: Multiple Attempts
            
            SystemDialog --> Granted: User Allows
            SystemDialog --> Denied: User Denies
            
            CustomDialog --> Settings: User Clicks Allow
            CustomDialog --> Denied: User Clicks Don't Allow
            
            Granted --> ButtonDisabled: Permission Granted
            Denied --> ButtonVisible: Try Again
            Settings --> SettingsApp: Open System Settings
        }
        
        ButtonDisabled --> NextEnabled: Navigation Available
    }
    
    PermissionScreen --> MainActivity: User Proceeds
    PermissionScreen --> SettingsApp: Manual Permission
    SettingsApp --> PermissionScreen: Return to App
    
    MainActivity --> [*]: App Running
```

## 🔗 Integration Points

### **External Dependencies**
- **AppIntro2 Library**: Provides slide-based onboarding framework
- **Android Permission System**: Manages notification permission requests
- **System Settings**: Fallback for manual permission configuration

### **Internal Dependencies**
- **MainActivity**: Navigation destination after permission granted
- **SplashActivity**: Entry point that launches IntroActivity
- **String Resources**: Internationalized text content
- **Layout Resources**: UI definition for permission fragment

## 🛠️ Configuration & Customization

### **Android Manifest Configuration**
```xml
<activity
    android:name=".activity.IntroActivity"
    android:exported="false"
    android:theme="@style/Theme.LifeCycle.AppCompat" />
```

### **Key Configuration Points**
- **Theme**: AppCompat theme for consistent styling
- **Export**: False - internal navigation only
- **Fragments**: Single NotificationPermissionFragment slide
- **Permissions**: POST_NOTIFICATIONS for Android 13+

## 📱 Platform Compatibility

| Android Version | Behavior |
|----------------|----------|
| **API < 26 (Android 8.0)** | Auto-granted, settings fallback |
| **API 26-32 (Android 8.0-12)** | Auto-granted, enhanced settings |
| **API 33+ (Android 13+)** | Full permission flow with dialogs |

## 🧪 Testing Scenarios

### **Critical Test Cases**
1. **Permission Grant Flow**: User allows on first attempt
2. **Permission Denial Flow**: User denies multiple times
3. **Settings Integration**: Manual permission via system settings
4. **Version Compatibility**: Behavior across Android versions
5. **Navigation Control**: Next button state management
6. **Memory Management**: Proper presenter lifecycle

### **Edge Cases**
- App backgrounded during permission dialog
- System settings unavailable
- Permission revoked while app running
- Fragment recreation during configuration changes

---

## 📝 Summary

The IntroActivity implements a robust, user-friendly permission onboarding flow using modern Android architecture patterns. Its MVP design ensures maintainability and testability while providing a seamless user experience across different Android versions and permission states.

**Key Features:**
- ✅ MVP Architecture with clean separation
- ✅ Infinite permission retry capability  
- ✅ Graceful fallback to system settings
- ✅ Cross-platform compatibility
- ✅ Comprehensive error handling
- ✅ Navigation flow control

This architecture serves as a foundation for scalable Android app development with complex permission requirements.