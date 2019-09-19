# VResult
Handle results nicely

## Example usage:


Fragment:

```kotlin
private val kanonViewModel: KanonViewModel by sharedViewModel()

kanonViewModel.bookmarks.observe(this, Observer { result ->
      when (result) {
        is VResult.Loading -> {
          recyclerView.showLoading()
        }
        is VResult.Success -> {
          dataSource.set(result.response)
          recyclerView.showContent()
        }
        is VResult.NoResult -> {
          val message = "You have no bookmarks saved. Click the create button to create a new bookmark"
          recyclerView.setMessage(message)
        }
        is VResult.Error -> {
          recyclerView.messageWithButton("Could not retrieve any bookmarks from Kanon, reason: ${result.exception.message}") {
            kanonViewModel.loadBookmarks()
          }
        }
      }
    })
```

ViewModel class:

```kotlin
class KanonViewModel(val kanonService: KanonService): ViewModel {
  var bookmarks: MutableLiveData<VResult<List<Bookmark>>> = MutableLiveData()

  fun loadBookmarks() {
    launchListOperation(bookmarks) {
      kanonService.userBookmarks().execute().body()
    }
  }
}
```
