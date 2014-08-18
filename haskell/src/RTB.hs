{-
Created       : 2014 Jul 17 (Thu) 08:38:10 by Harold Carr.
Last Modified : 2014 Aug 18 (Mon) 08:11:47 by Harold Carr.

- based on
  - http://stackoverflow.com/questions/24784883/using-threepenny-gui-reactive-in-client-server-programming
  - threepenny-gui MissingDollars sample
-}

{-# LANGUAGE RecursiveDo #-}

module RTB where

import           Data.List                   (elemIndex, elemIndices)
import qualified Data.Map                    as Map
import           Data.Maybe                  (fromJust, fromMaybe)
import           Data.RDF.Types              (LValue (..), Node (..))
import qualified Data.Text                   as T (pack, unpack)
import           Database.HSparql.Connection
import           Debug.Trace
import qualified Graphics.UI.Threepenny      as UI
import           Graphics.UI.Threepenny.Core
import           RTBQ

hcDebug :: c -> String -> c
hcDebug = flip trace

defaultEndPoint :: String
defaultEndPoint = "http://localhost:3030/ds/query"

data SPOType = SUB | PRE | OBJ deriving Eq

instance Show SPOType where
    show SUB = "?subject"
    show PRE = "?predicate"
    show OBJ = "?object"

main :: IO ()
main = startGUI defaultConfig $ \w -> do
    return w # set title "RDF Triple Browser"
    getBody w #+ [ mkLayout ]
    return ()

mkLayout :: UI Element
mkLayout  = mdo
    -- input elements
    sparqlEndpointURL <- UI.input  # set (attr "size") "175" # set (attr "type") "text"
                                   # set (attr "value") defaultEndPoint
    submitBtn         <- UI.button #+ [string "submit"]

    -- input and display elements and more
    (subLBSelection, subClrBtn, subLayout, eSubLBSelection, bSubDB, hSubQFillLB) <- mkSPOPanel SUB
    (preLBSelection, preClrBtn, preLayout, ePreLBSelection, bPreDB, hPreQFillLB) <- mkSPOPanel PRE
    (objLBSelection, objClrBtn, objLayout, eObjLBSelection, bObjDB, hObjQFillLB) <- mkSPOPanel OBJ

    -- display elements
    frame    <- UI.frame # set (attr "name")   "top"
                         # set (attr "target") "top"
                         # set (attr "src")    "http://haroldcarr.com/"
    frameset <- UI.frameset #+ [ element frame ]

    -- action

    -- SUBMIT
    on UI.click submitBtn $ \_ -> do
        liftIO $ putStrLn "submit"
        query aTrueBoundNodePair aTrueBoundNodePair aTrueBoundNodePair
        return ()

    -- SUBJECT
    on UI.click subClrBtn   $ \_  -> do
        element subLBSelection # set value (show SUB);
        slt SUB Nothing True
    onEvent eSubLBSelection $ \mk ->
        slt SUB mk      False

    -- PREDICATE
    on UI.click preClrBtn   $ \_  -> do
        element preLBSelection # set value (show PRE);
        slt PRE Nothing True
    onEvent ePreLBSelection $ \mk ->
        slt PRE mk      False

    -- OBJECT
    on UI.click objClrBtn   $ \_  -> do
        element objLBSelection # set value (show OBJ);
        slt OBJ Nothing True
    onEvent eObjLBSelection $ \mk ->
        slt OBJ mk      False

    let query :: (Bool, BindingValue) -> (Bool, BindingValue) -> (Bool, BindingValue) -> UI ()
        query s p o = do
            url <- get value sparqlEndpointURL;
            liftIO $ putStrLn ("query " ++ show url ++ " " ++ show s ++" " ++ show p ++ " " ++ show o)
            (s',p',o') <- liftIO $ query2 url s p o
            -- These are the MAGIC steps.  A Handler feeds events to its corresponding Event (from newEvent)
            mapM_ liftIO [hSubQFillLB s', hPreQFillLB p', hObjQFillLB o']
            return ()

        setSelAndFrame lbSel k db0 = do
            element lbSel # set value        v1 -- shortened version
            element frame # set (attr "src") v2 -- full version
          where
            (v1,bn) = (fromJustLookup k db0)
            (v2,_)  = mkDI False bn

        fromJustLookup n db0 = fromJust $ dbLookup n db0
        sndLookup      n db0 = (False, snd (fromJustLookup n db0))

        varOrSelection :: String -> DB DI -> (Bool, BindingValue)
        varOrSelection lbSel db0 =
            if lbSel `elem` [show SUB, show PRE, show OBJ]
                then aTrueBoundNodePair
                else sndLookup 0 db0

        slt :: SPOType -> Maybe DBKey -> Bool -> UI ()
        slt spoType mk isClear = do
            liftIO $ putStrLn ("slt " ++ show spoType ++ " " ++ show mk)
            [sDB   , pDB   , oDB   ] <- mapM currentValue [bSubDB        , bPreDB        , bObjDB        ]
            [sLBSel, pLBSel, oLBSel] <- mapM (get value)  [subLBSelection, preLBSelection, objLBSelection]
            case mk of
                Just k -> case spoType of
                                 SUB -> setSelAndFrame subLBSelection k sDB
                                 PRE -> setSelAndFrame preLBSelection k pDB
                                 OBJ -> setSelAndFrame objLBSelection k oDB
                _      -> element frame
            let tval     = aTrueBoundNodePair
                fval     = sndLookup (fromJust mk)
                pick spo lbSel db0 = if spoType == spo then
                                         if isClear then tval else fval db0
                                         else varOrSelection lbSel db0
            query (pick SUB sLBSel sDB)
                  (pick PRE pLBSel pDB)
                  (pick OBJ oLBSel oDB)

        aTrueBoundNodePair   :: (Bool, BindingValue)
        aTrueBoundNodePair   =  (True, aBoundNode)

    -- layout
    grid [ [ row [ element sparqlEndpointURL, element submitBtn ] ]
         , [ row [ element subLayout, element preLayout, element objLayout ] ]
         , [ element frameset ]
         ]

mkSPOPanel :: SPOType
           -> UI ( UI.Element -- current selection
                 , UI.Element -- clrBtn
                 , UI.Element -- layout
                 , Event (Maybe DBKey)
                 , Behavior (DB DI)
                 , Handler [BindingValue]
                 )
mkSPOPanel spoType = mdo
    -- GUI elements
    lbSelection <- UI.input  # set (attr "size") "40" # set (attr "type") "text"
                             # set value (show spoType)
    clrBtn      <- UI.button #+ [string "*"]
    expandBtn   <- UI.button #+ [string "+"] # set value "+"
    listBox     <- UI.listBox  bLBItems bLBSelection bDisplayDI
    element listBox # set (attr "size") "10" # set style [("width","300px")]

    let eExpandBtn   :: Event ()
        eExpandBtn   = UI.click expandBtn

        eLBSelection :: Event (Maybe DBKey)
        eLBSelection = rumors $ UI.userSelection listBox

    onEvent eExpandBtn $ \_ -> do
        current <- get value expandBtn
        let next = if current == "+" then "-" else "+"
        element expandBtn # set text next # set value next `hcDebug` ("onEvent eExpandBtn c " ++ current ++ " n " ++ next)

    (eQueryFillLB, hQueryFillLB) <- liftIO newEvent

    -- bDB :: Behavior (DB DI)
    bDB <- accumB dbEmpty $ concatenate <$> unions
        [ dbQueryFill  <$> eQueryFillLB
        , dbExpandFill <$  eExpandBtn
        ]

    -- bLBSelection :: Behavior (Maybe DBKey)
    bLBSelection <- stepper Nothing eLBSelection `hcDebug` "stepper"

    let dbQueryFill    :: [BindingValue] -> DB DI -> DB DI
        dbQueryFill ss (DB sP _ _) = foldr (dbInsert . mkDI sP) (dbEmpty' sP) ss `hcDebug` ("dbQueryFill " ++ show sP)

        dbExpandFill   :: DB DI -> DB DI
        dbExpandFill (DB sP k db0) = DB (not sP) k $ Map.map (\(s,b) -> if sP then mkDI (not sP) b else (shorten s, b)) db0 `hcDebug` ("dbExpandFill " ++ show sP)

        bLookup        :: Behavior (DBKey -> Maybe DI)
        bLookup        = flip dbLookup <$> bDB `hcDebug` "bLookup"

        bDisplayDI     :: Behavior (DBKey -> UI Element)
        bDisplayDI     = (UI.string .) <$> (maybe "" showDI .) <$> bLookup

        bLBItems       :: Behavior [DBKey]
        bLBItems       = dbKeys <$> bDB

    layout <- column [ element lbSelection
                     , row [ element clrBtn, element expandBtn ]
                     , element listBox
                     ]

    return (lbSelection, clrBtn, layout, eLBSelection, bDB, hQueryFillLB)

aBoundNode :: BindingValue
aBoundNode =  Bound (UNode (T.pack "A DUMMY NODE"))

------------------------------------------------------------------------------
-- DB Model

type DBKey = Int
data DB a  = DB { shortened :: !Bool, nextKey :: !Int, db :: Map.Map DBKey a }

dbEmpty  :: DB a
dbEmpty  = DB True 0 Map.empty

dbEmpty' :: Bool -> DB a
dbEmpty' b = DB b 0 Map.empty

dbSize   :: DB a -> Int
dbSize   = Map.size . db

dbKeys   :: DB a -> [DBKey]
dbKeys   = Map.keys . db

dbInsert :: a -> DB a -> DB a
dbInsert x   (DB s nk db0) = DB s (nk+1) $ Map.insert nk x db0

dbLookup :: DBKey -> DB a -> Maybe a
dbLookup key (DB _ _  db0) =               Map.lookup key  db0 `hcDebug` ("dbLookup " ++ show key)

------------------------------------------------------------------------------
-- What is stored in data base

type DI = (String, BindingValue)

showDI :: DI -> String
showDI (x,_) = x

mkDI :: Bool -> BindingValue -> (String, BindingValue)
mkDI shortenP b@(Bound v) =
    let s = T.unpack $ case v of
                           (UNode x)             -> x
                           (BNode x)             -> x
                           --    BNodeGen x            -> show x
                           (LNode (PlainL  x))   -> x
                           (LNode (PlainLL x _)) -> x
                           (LNode (TypedL  x _)) -> x

    in (if shortenP then shorten s else s, b)
mkDI _ Unbound   = (show Unbound, Unbound)

------------------------------------------------------------------------------
-- Utility

shorten :: String -> String
shorten x = fromMaybe x $ maybeShorten x

maybeShorten :: String -> Maybe String
maybeShorten x = subStringAfterFirstSharp x <|> subStringAfterLastSlash x

subStringAfterFirstSharp :: String -> Maybe String
subStringAfterFirstSharp x = do
    i <- elemIndex '#' x
    return $ drop (i + 1) x

subStringAfterLastSlash :: String -> Maybe String
subStringAfterLastSlash x0 = do
    let x  = removeTrailingSlash x0
        is = elemIndices '/' x
    return $ if null is
                 then x
                 else drop (last is + 1) x

removeTrailingSlash :: String -> String
removeTrailingSlash x | last x == '/' = take (length x - 1) x
                      | otherwise     = x

-- End of file.
