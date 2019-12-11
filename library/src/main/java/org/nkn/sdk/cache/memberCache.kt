package org.nkn.sdk.cache

import android.util.LruCache

const val MULTI_CLIENT_CACHE_SIZE = 4 * 1024 * 1024
const val SHARED_KEY_CACHE_SIZE = 16 * 1024 * 1024

val multiClientCache = LruCache<String, Any?>(MULTI_CLIENT_CACHE_SIZE)
val sharedKeyCache = LruCache<ByteArray, ByteArray>(SHARED_KEY_CACHE_SIZE)
