;;; ertb.el --- git          -*- lexical-binding: t; -*-

;;; Commentary:

;;; Code:

(require 'window-layout)

(setq wm
      (wlf:layout
       '(- (:upper-max-size 1)
           query1
           (- (:upper-max-size 1)
              query2
              (| (:left-size-ratio 0.32)
                 subject
                 (| (:left-size-ratio 0.5)
                    predicate
                    object))))
       '(
         (:name query1
          :buffer "query buffer")
         (:name query2
          :buffer "query buffer")
         (:name subject
          :buffer "subject buffer")
         (:name predicate
          :buffer "predicate buffer")
         (:name object
          :buffer "object buffer")
        )))

;; Window controlling
(wlf:show    wm 'subject)
(wlf:hide    wm 'subject)
(wlf:toggle  wm 'subject)
(wlf:select  wm 'subject)
(wlf:toggle-maximize  wm 'subject)

;; Window updating
(wlf:refresh wm)
(wlf:reset-window-sizes wm)
(wlf:reset-init wm)

;; Accessing a buffer
(wlf:get-buffer wm 'subject) ; -> <#buffer object or buffer name>
(wlf:set-buffer wm 'subject "*scratch*")

;; Accessing a window
(wlf:get-window wm 'subject) ; -> <#window object>

;; Layout hook
(defun wlf:test-hook (wset) (message "HOOK : %s" wset))
(wlf:layout-hook-add wm 'wlf:test-hook)
(wlf:layout-hook-remove wm 'wlf:test-hook)

;;

(provide 'ertb)

;;; ertb.el ends here
