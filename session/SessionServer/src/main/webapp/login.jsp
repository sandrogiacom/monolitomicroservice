<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
    <head>
        <title>Login</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    </head>

    <body>
        <form method="post" action='<%= response.encodeURL("j_security_check") %>'>
            <table>
                <tr>
                    <th>Login:</th>
                    <td>
                        <input type="text" autocorrect="off" autocapitalize="none" id="j_username" name="j_username" tabindex="1"/>
                    </td>
                </tr>
                <tr>
                    <th>Password:</th>
                    <td>
                        <input type="password" id="j_password" name="j_password" tabindex="2" />
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input id="submitLogin" value="Acessar" tabindex="3" type="submit" />
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>
