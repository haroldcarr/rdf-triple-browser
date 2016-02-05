{-# OPTIONS_GHC -fno-warn-unused-do-bind #-}

module JaccoUnsafeFRPExample where

import           Graphics.UI.Threepenny as UI
import           System.Random

jacco :: IO ()
jacco = startGUI defaultConfig setup

setup :: Window -> UI ()
setup window =
    do btn  <- UI.button #+ [string "click"]
       area0 <- UI.input

       getBody window #+ [element btn, element area0]

       let e = unsafeMapIO (const sideEffects) (UI.click btn)
       b <- stepper "" e

       return area0 # sink value b

       return ()

sideEffects :: IO String
sideEffects =
    do x <- randomRIO (0,10) :: IO Int
       return (show x)
