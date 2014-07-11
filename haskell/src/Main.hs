{-
Created       : 2014 Jul 11 (Fri) 03:11:49 by Harold Carr.
Last Modified : 2014 Jul 11 (Fri) 03:17:18 by Harold Carr.
-}

-- https://ocharles.org.uk/blog/posts/2013-12-07-24-days-of-hackage-threepenny-gui.html

module Main where

import           Data.Map               as M
import           Data.Set               as S
import           Graphics.UI.Threepenny

type UserName = String
type ToDoList = S.Set String
type Database = M.Map UserName ToDoList

main :: IO ()
main = startGUI defaultConfig setup -- will say port in CLI when starting up

setup :: Window -> UI ()
setup rootWindow = undefined

-- End of file.
