## Design

The Simple Design is below:

![simple_design (1)](https://github.com/shiho198611/AuthmeAppTesting/assets/5583958/d9883dea-dafb-4710-afb0-a7724817db12)

And the detail data flow:

![data_flow](https://github.com/shiho198611/AuthmeAppTesting/assets/5583958/9387b745-b872-486e-b45c-1ae6d3b64d4b)


- All data is obtained from web service, but always provide data from the database.
- App layer only via repositories(in SDK) to obtain data.
- The app layer uses MVVM architecture.

## Use Library

### UI
- Jetpack compose
- Glide
- Navigation

### Architecture
- Coroutines
- Paging3
- Koin

### Data
- Retrofit2
- Room
- Moshi

### Unit test
- Mockito
- MockK
- JUnit4

## How to use

First, need initial SDK, at application or main activity.
```kotlin
SDKInitialHelper.initSDKInstance(
    SDKInitialFactoryImpl(this, "YOUR TOKEN")
)
```

And create repostory instance to access data.
```kotlin
// For review github user.
GithubUsersRepository()
// For search github user.
SearchUsersRepository()
```
