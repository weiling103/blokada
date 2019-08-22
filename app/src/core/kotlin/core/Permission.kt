package core

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.coroutines.CompletableDeferred

internal var deferred = CompletableDeferred<Boolean>()

internal const val REQUEST_STORAGE = 2

fun askStoragePermission(ktx: Kontext, act: Activity) = {
    v("asking for storage permissions")
    if (Build.VERSION.SDK_INT >= 23) {
        deferred.completeExceptionally(Exception("new permission request"))
        deferred = CompletableDeferred()
        if (checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            deferred.complete(true)
        } else act.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE)
    } else deferred.complete(true)
    deferred
}()

fun storagePermissionResult(ktx: Kontext, code: Int) = {
    v("received storage permissions response", code)
    when {
        deferred.isCompleted -> Unit
        code == PackageManager.PERMISSION_GRANTED -> deferred.complete(true)
        else -> deferred.completeExceptionally(Exception("permission result: $code"))
    }
}()

fun checkStoragePermissions(ktx: AndroidKontext) = {
    if (Build.VERSION.SDK_INT >= 23) checkSelfPermission(ktx.ctx,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    else true
}()
