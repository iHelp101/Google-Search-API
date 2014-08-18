package com.mohammadag.googlesearchapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;


public class GoogleSearchAPIModule implements IXposedHookLoadPackage, IXposedHookZygoteInit {

	private Context mContext = null;
	private static ArrayList<Intent> mQueuedIntentList = null;
	private static XSharedPreferences mPreferences;
	 
	String Checker = "";

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		mPreferences = new XSharedPreferences("com.mohammadag.googlesearchapi");
	}
		
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.mohammadag.googlesearchapi")) {
			touchOurself(lpparam.classLoader);
		}

		if (!lpparam.packageName.equals(Constants.GOOGLE_SEARCH_PACKAGE))
			return;

		Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        Context context = (Context) callMethod(activityThread, "getSystemContext");

        String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
		
        Checker = versionName;
        
		/* IPC, not sure how many processes Google Search runs in, but we need this since
		 * it's surely not one.
		 */
		final BroadcastReceiver internalReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.INTENT_SETTINGS_UPDATED.equals(intent.getAction())) {
					mPreferences.reload();
				} else if (Constants.INTENT_QUEUE_INTENT.equals(intent.getAction())) {
					Intent intentToQueue = intent.getParcelableExtra(Constants.KEY_INTENT_TO_QUEUE);
					mQueuedIntentList.add(intentToQueue);
				} else if (Constants.INTENT_FLUSH_INTENTS.equals(intent.getAction())) {
					for (Intent intentToFlush : mQueuedIntentList) {
						XposedBridge.log("Sending queued intent");
						sendBroadcast(mContext, intentToFlush);
						mQueuedIntentList.remove(intentToFlush);
					}
				}
			}
		};
		
		String SC = null;
		String VSCL = null;
		String SRF = null;
		String SOI = null;
		String MVSCL = null;
		String CharSq = null;
		String CharSq2 = null;
		
		XposedBridge.log("Google Search Version" +Checker);
		
		//Hooks
		SC = "bpn";
		VSCL = "bpy";
		SRF = "cby";
		SOI = "cuc";
			
			
		MVSCL = "a";
		CharSq = "hmu";
		CharSq2 = "cbs";
		
		
		if (Checker.equals("3.5.16.1262550.arm")) {
			SC = "bir";
			VSCL = "bjb";
			SRF = "bur";
			SOI = "cmh";
			
			
			MVSCL = "a";
			CharSq = "heb";
			CharSq2 = "bul";
		} 
		
		if (Checker.equals("3.5.15.1254529.arm") || Checker.equals("3.5.14.1234234.arm")) {
				SC = "bir";
				VSCL = "bjb";
				SRF = "bur";
				SOI = "cmh";
				
				
				MVSCL = "a";
				CharSq = "hea";
				CharSq2 = "bul";
		}
		

		// com.google.android.search.shared.api.Query
		Class<?> Query = findClass("com.google.android.shared.search.Query", lpparam.classLoader);

        // com.google.android.search.core.SearchController
        // Google Search V3.4: azs
        // Google Search V3.5: bir
        	Class<?> SearchController = findClass(SC, lpparam.classLoader);

		// com.google.android.search.core.SearchController$MyVoiceSearchControllerListener
        // Google Search V3.4: bae
        // Google Search V3.5: bjb
		Class<?> MyVoiceSearchControllerListener = findClass(VSCL, lpparam.classLoader);
		
		// com.google.android.search.core.prefetch.SearchResultFetcher
        // Google Search V3.4: blq
        // Google Search V3.5: bur
		Class<?> SearchResultFetcher = findClass(SRF, lpparam.classLoader);
		
		// com.google.android.search.gel.SearchOverlayImpl
        // Google Search V3.4: ccu
        // Google Search V3.5: cmh
		Class<?> SearchOverlayImpl = findClass(SOI, lpparam.classLoader);

		XposedBridge.hookAllConstructors(SearchController, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				//Hooks
				String mCon = "mContext";
				
				final Object thisObject = param.thisObject;
				mContext = (Context) getObjectField(param.thisObject, mCon);
				mQueuedIntentList = new ArrayList<Intent>();
				mContext.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String string = intent.getStringExtra(GoogleSearchApi.KEY_TEXT_TO_SPEAK);
						if (TextUtils.isEmpty(string))
							return;

						
						//Hooks
						String VSS = "mVoiceSearchServices";
						String VTS = "aCT";
					    String ttMan = "a";
						
						if (Checker.equals("3.5.15.1254529.arm") || Checker.equals("3.5.14.1234234.arm") || Checker.equals("3.5.16.1262550.arm")) {
									VSS = "mVoiceSearchServices";
									VTS = "azL";
								    ttMan = "a";
						}
								
							
						
						Object mVoiceSearchServices = getObjectField(thisObject, VSS);
						// getLocalTtsManager
                        // Google Search V3.4: asi
                        // Google Search V3.5: azL
						Object ttsManager = XposedHelpers.callMethod(mVoiceSearchServices, VTS);
						try {
                            // Google Search V3.4: a
                            // Google Search V3.5: a
							XposedHelpers.callMethod(ttsManager, ttMan, string, null, 0);
						} catch (NoSuchMethodError e) {
							e.printStackTrace();
							try {
								Field f = XposedHelpers.findFirstFieldByExactType(ttsManager.getClass(),
										TextToSpeech.class);
								f.setAccessible(true);
								TextToSpeech speech = (TextToSpeech) f.get(ttsManager);
								speech.speak(string, TextToSpeech.QUEUE_FLUSH, null);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				}, new IntentFilter(GoogleSearchApi.INTENT_REQUEST_SPEAK));

				IntentFilter iF = new IntentFilter();
				iF.addAction(Constants.INTENT_SETTINGS_UPDATED);
				iF.addAction(Constants.INTENT_FLUSH_INTENTS);
				iF.addAction(Constants.INTENT_QUEUE_INTENT);

				mContext.registerReceiver(internalReceiver, iF);
			}
		});

		// obtainSearchResult
        // Google Search V3.4: s
        // Google Search V3.5: w
		
		
		//Hooks
		String SRFQ = "x";
		if (Checker.equals("3.5.16.1262550.arm")) {
			SRFQ = "w";
		}
		if (Checker.equals("3.5.15.1254529.arm") || Checker.equals("3.5.14.1234234.arm")) {
			SRFQ = "w";
		} 
				
		
		findAndHookMethod(SearchResultFetcher, SRFQ, Query, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				

				//Hooks
				String qR = "bqk";
				String mCac = "mCache";
				String mClo = "mClock";
				String mC = "a";
				
				if (Checker.equals("3.5.16.1262550.arm")) {
					qR = "bkt";
					mCac = "mCache";
					mClo = "mClock";
					mC = "a";
				}
				if (Checker.equals("3.5.15.1254529.arm")) {
						qR = "mQueryChars";
						mCac = "mCache";
						mClo = "mClock";
						mC = "a";
				}
				if (Checker.equals("3.5.14.1234234.arm")) {
							qR = "mQueryChars";
							mCac = "mCache";
							mClo = "mClock";
							mC = "get";
				}
						
				
				Object queryResult = param.args[0];
				CharSequence searchQueryText = (CharSequence) getObjectField(queryResult, qR);
				Object mCache = getObjectField(param.thisObject, mCac);
				Object mClock = getObjectField(param.thisObject, mClo);
				Object mCachedResult = XposedHelpers.callMethod(mCache, mC, queryResult,
						XposedHelpers.callMethod(mClock, "elapsedRealtime"),
						true);
				
				mPreferences.reload();

				/* Not doing this causes a usability issue. If the user has a search showing
				 * results, and they tap the mic, then cancel the voice search, then the search
				 * is handled again, thus throwing the user in an infinite loop of pressing back,
				 * until the user figures it out and uses the task switcher to close Google Search.
				 */
				if (mCachedResult != null && mContext != null
						&& mPreferences.getBoolean(Constants.KEY_PREVENT_DUPLICATES, true)) {
					return;
				}

				if (mContext != null) {
					broadcastGoogleSearch(mContext, searchQueryText, false,
							mPreferences.getBoolean(Constants.KEY_DELAY_BROADCASTS, false));
				} else {
					XposedBridge.log(String.format("Google Search API: New Search detected: %s",
							searchQueryText.toString()));
				}
			}
		});

		// onRecognitionResult
        // Google Search V3.4: s // a(CharSequence paramCharSequence, glq paramglq, blk paramblk)
        // Google Search V3.5: w // a(CharSequence paramCharSequence, hea paramhea, bul parambul)
		findAndHookMethod(MyVoiceSearchControllerListener, MVSCL, CharSequence.class, findClass(CharSq, lpparam.classLoader), findClass(CharSq2, lpparam.classLoader), new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				CharSequence voiceResult = (CharSequence) param.args[0];
				mPreferences.reload();
				if (mContext != null) {
					broadcastGoogleSearch(mContext, voiceResult, true,
							mPreferences.getBoolean(Constants.KEY_DELAY_BROADCASTS, false));
				} else {
					XposedBridge.log(voiceResult.toString());
				}
			}
		});

		/* GEL workaround, GEL opens Google Search eventually, so this will overlay whatever
		 * activity a developer has made. This broadcasts intents after the window has gained focus.
		 */				
		findAndHookMethod(SearchOverlayImpl, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				boolean hasFocus = (Boolean) param.args[0];

				Context context = (Context) getObjectField(param.thisObject, "mContext");
				if (context != null && !hasFocus)
					context.sendBroadcast(new Intent(Constants.INTENT_FLUSH_INTENTS));
			}
		});
	}
	
	private void touchOurself(ClassLoader classLoader) {
		findAndHookMethod("com.mohammadag.googlesearchapi.UiUtils", classLoader,
				"isHookActive", XC_MethodReplacement.returnConstant(true));
	}
	
	
	
	private static void broadcastGoogleSearch(Context context, CharSequence searchText, boolean voice, boolean delayed) {
		Intent intent = new Intent(GoogleSearchApi.INTENT_NEW_SEARCH);
		intent.putExtra(GoogleSearchApi.KEY_VOICE_TYPE, voice);
		intent.putExtra(GoogleSearchApi.KEY_QUERY_TEXT, searchText.toString());
		if (delayed) {
			mQueuedIntentList.add(intent);
		} else {
			sendBroadcast(context, intent);
		}
	}
	
	private static void sendBroadcast(Context context, Intent intent) {
		context.sendBroadcast(intent, Constants.PERMISSION);
	}
}

