// only used with launchOperation
interface VResultOperationable<out T> {
  fun getList(): List<T>?
}

sealed class VResult<out T> {
  data class Success<T>(val response: T) : VResult<T>()
  data class Error<T>(val exception: Exception?) : VResult<T>()
  object Loading : VResult<Nothing>()
  object NoResult : VResult<Nothing>()
  object Clear : VResult<Nothing>()
  object NoInternet : VResult<Nothing>()

  fun tryGetResponse(): T? {
    return when (this) {
      is Success -> {
        response
      }
      else -> null
    }
  }
}

fun <T> ViewModel.launchOperation(liveData: MutableLiveData<VResult<T>>, func: () -> T?) {
  liveData.postValue(VResult.Loading)

  viewModelScope.launch {
    withContext(Dispatchers.Default) {

      try {
        val singleResult = func()

        if (singleResult == null) {
          liveData.postValue(VResult.Error(null))
          return@withContext
        }

        if (singleResult is VResultOperationable<*>) {
          val list = singleResult.getList()
          when {
            list == null -> liveData.postValue(VResult.Error(null))
            list.isEmpty() -> liveData.postValue(VResult.NoResult)
            else -> liveData.postValue(VResult.Success(singleResult))
          }
        } else {
          when (singleResult) {
            null -> liveData.postValue(VResult.Error(null))
            else -> liveData.postValue(VResult.Success(singleResult))
          }
        }
      } catch (exception: Exception) {
        Timber.e(exception)
        liveData.postValue(VResult.Error(exception))
      }
    }
  }
}

fun <T> ViewModel.launchListOperation(liveData: MutableLiveData<VResult<List<T>>>, function: () -> List<T>?) {
  liveData.postValue(VResult.Loading)

  viewModelScope.launch {
    withContext(Dispatchers.Default) {

      try {

        val result = function()
        when {
          result == null -> liveData.postValue(VResult.Error(null))
          result.isEmpty() -> liveData.postValue(VResult.NoResult)
          else -> liveData.postValue(VResult.Success(result))
        }
      } catch (exception: Exception) {
        Timber.e(exception)
        liveData.postValue(VResult.Error(exception))
      }
    }
  }
}
