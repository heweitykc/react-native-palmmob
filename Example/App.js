/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  TouchableHighlight,
  StatusBar,
  NativeModules,
  PermissionsAndroid
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,  
} from 'react-native/Libraries/NewAppScreen';

import duomofg from './duomofg';

const {  OAIDUtil,DyAdApi,XFYunLATModule } = NativeModules

let uid = '1EW19NCT2WHH7PtJQaG0';

function initOAID(){
  OAIDUtil.getDeviceID().then( resp => {
      console.log(resp)
    }
  );
}

const requestRecordAudioPermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
      {
        title: "Cool Photo App Camera Permission",
        message:
          "Cool Photo App needs access to your camera " +
          "so you can take awesome pictures.",
        buttonNeutral: "Ask Me Later",
        buttonNegative: "Cancel",
        buttonPositive: "OK"
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log("You can use the camera");
    } else {
      console.log("Camera permission denied");
    }
  } catch (err) {
    console.warn(err);
  }
};

async function requestPermission(){
  await requestRecordAudioPermission();
}

function initGame(){
  DyAdApi.init(duomofg.appid, duomofg.secret, 'ddstar')
}

function jumpAdList(){
  DyAdApi.jumpAdList(uid,0)
}

function jumpMine(){
  DyAdApi.jumpMine(uid)
}

function startRecord(){
  XFYunLATModule.startRecord();
}

function stopRecord(){
  XFYunLATModule.stopRecord();
}

const App: () => React$Node = () => {
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          {global.HermesInternal == null ? null : (
            <View style={styles.engine}>
              <Text style={styles.footer}>Engine: Hermes</Text>
            </View>
          )}
          <View style={styles.body}>
          <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={requestPermission}>
              <Text>{'requestPermission'}</Text>
            </TouchableHighlight>               
          <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={startRecord}>
              <Text>{'startRecord'}</Text>
            </TouchableHighlight>   
            <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={stopRecord}>
              <Text>{'stopRecord'}</Text>
            </TouchableHighlight>   
            <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={initOAID}>
              <Text>{'测试OAID>'}</Text>
            </TouchableHighlight>   
            <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={initGame}>
              <Text>{'初始化游戏试玩>'}</Text>
            </TouchableHighlight>
            <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={jumpAdList}>
              <Text>{'游戏列表>'}</Text>
            </TouchableHighlight>            
            <TouchableHighlight style={styles.login_phone} underlayColor='transparent' activeOpacity={0.95} onPress={jumpMine}>
              <Text>{'打开我的>'}</Text>
            </TouchableHighlight>                        
            <LearnMoreLinks />
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  login_phone:{
    margin: 20
  },
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
