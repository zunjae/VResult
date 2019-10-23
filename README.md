# VResult
Handle results nicely

## Example usage:

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

Fragment:

```kotlin
viewModel.bookmarks.observe(this, Observer { result ->
      when (result) {
        is VResult.Loading -> {
          // show loading
        }
        is VResult.Success -> {
          dataSource.set(result.response)
        }
        is VResult.NoResult -> {
          // show no results
        }
        is VResult.Error -> {
          // show error
        }
      }
    })
```


