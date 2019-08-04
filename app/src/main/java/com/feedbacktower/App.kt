package com.feedbacktower

import android.app.Application
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.db.AppDatabase
import com.feedbacktower.data.network.HomeDataSource
import com.feedbacktower.data.network.HomeDataSourceImpl
import com.feedbacktower.data.repository.HomeDataRepository
import com.feedbacktower.data.repository.HomeDataRepositoryImpl
import com.feedbacktower.network.utils.ConnectivityReceiver
import com.feedbacktower.ui.home.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class App : Application(), KodeinAware {
    override val kodein: Kodein
        get() = Kodein.lazy {
            import(androidXModule(this@App))
           /* bind() from singleton { AppDatabase.getInstance(instance()) }
            bind() from singleton { instance<AppDatabase>().userDao() }
            bind() from singleton { instance<AppDatabase>().adsDao() }
*/
         /*   //home
            bind<HomeDataSource>() with singleton { HomeDataSourceImpl(instance()) }
            bind<HomeDataRepository>() with singleton { HomeDataRepositoryImpl(instance()) }
            bind() from provider { HomeViewModelFactory(instance()) }*/
        }


    companion object {
        private var mInstance: App? = null
        @Synchronized
        fun getInstance(): App {
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    fun getToken(): String? {
        return AppPrefs.getInstance(this).authToken
    }
}