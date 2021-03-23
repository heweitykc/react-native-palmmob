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
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,  
} from 'react-native/Libraries/NewAppScreen';

import duomofg from './duomofg';

const {  OAIDUtil,DyAdApi } = NativeModules

let uid = '1EW19NCT2WHH7PtJQaG0';

function initOAID(){
  OAIDUtil.getDeviceID().then( resp => {
      console.log(resp)
    }
  );
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