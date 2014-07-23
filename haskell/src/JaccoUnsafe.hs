module JaccoUnsafe where

import           Graphics.UI.Threepenny as UI
import           System.Random

jacco :: IO ()
jacco = startGUI defaultConfig setup

setup :: Window -> UI ()
setup window =
    do btn  <- UI.button #+ [string "click"]
       area <- UI.input

       getBody window #+ [element btn, element area]

       let e = unsafeMapIO (\_ -> sideEffects) (UI.click btn)
       b <- stepper "" e

       return area # sink value b

       return ()

sideEffects :: IO String
sideEffects =
    do x <- randomRIO (0,10) :: IO Int
       return (show x)
