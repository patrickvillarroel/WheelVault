package io.github.patrickvillarroel.wheel.vault.data.datasource.image

import coil3.key.Keyer
import coil3.request.Options
import io.github.jan.supabase.storage.StorageItem

class StorageItemKeyer : Keyer<StorageItem> {
    override fun key(data: StorageItem, options: Options): String? =
        "${data.bucketId}/${data.path}?auth=${data.authenticated}"
}
