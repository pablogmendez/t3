package app

import (
    "html/template"
	"net/http"
    "appengine"
    "appengine/datastore"
)

// [START greeting_struct]
type Guest struct {
    Name  string
    LastName string
    Email string
    Company string
    Confirmation bool
}
// [END greeting_struct]

func init() {
	http.HandleFunc("/confirm", confirm)
	http.HandleFunc("/query", query)
	http.HandleFunc("/list", list)
}

// [START func_confirm]
func confirm(w http.ResponseWriter, r *http.Request) {
    // [START new_context]	
	c := appengine.NewContext(r)
	// [END new_context]        
    c.Debugf("Entrando a la funcion confirm del servicio default: %v", r.URL)

	g := Guest{
	    Name: r.FormValue("name"),
	    LastName: r.FormValue("lastname"),
	    Email: r.FormValue("email"),
	    Company: r.FormValue("company"),
	    Confirmation: true,
	}

    key := datastore.NewIncompleteKey(c, "Guest", datastore.NewKey(c, "Confirm", "default_guest", 0, nil))
    _, err := datastore.Put(c, key, &g)
    if err != nil {
            http.Error(w, err.Error(), http.StatusInternalServerError)
            return
    }
    http.Redirect(w, r, "/", http.StatusFound)
}
// [END func_confirm]

// [START func_root]
func root(w http.ResponseWriter, r *http.Request) {
    // [START new_context]	
	c := appengine.NewContext(r)
	// [END new_context]        
    c.Debugf("Entrando a la funcion root del servicio default: %v", r.URL)
    guest := make([]Guest, 0, 10)
    guestbookTemplate.Execute(w, guest)
}
// [END func_root]