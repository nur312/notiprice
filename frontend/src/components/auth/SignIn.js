import { useForm } from "react-hook-form";
import { TELEGRAM_BOT } from "../Constants";
import "./Auth.css";

function SignIn(props) {
    const { register, handleSubmit, formState } = useForm();
    const { errors } = formState
    const onSubmit = (data) => {
        console.log(data);
        props.login(data, "ok")
    };
    return (
        <div className="authContainer">
            <p className="invisible">Prrr</p>
            <form onSubmit={handleSubmit(onSubmit)}>
                <h2>Welcome to Notiprice</h2>
                <div className="ui divider"></div>
                <div className="ui form">
                    <div className="field">
                        <label>Your username</label>
                        <input
                            type="text"
                            placeholder="Username"
                            {...register("username", {
                                required: "Username is required",
                                minLength: {
                                    value: 5,
                                    message: "Password must be more than 5 characters",
                                },
                                pattern: {
                                    value: /^[a-z0-9]*$/i,
                                    message: "Only lowercase letters and numbers are allowed"
                                }
                            })}
                        />
                    </div>
                    <p className="authP">{errors?.username?.message}</p>

                    <div className="field">
                        <label>Password</label>
                        <input
                            type="password"
                            placeholder="Password"
                            {...register("password",
                                {
                                    required: "Password is required",
                                    minLength: {
                                        value: 4,
                                        message: "Password must be more than 4 characters",
                                    },
                                    pattern: {
                                        value: /^[a-z0-9]*$/i,
                                        message: "Only lowercase letters and numbers are allowed"
                                    }
                                })}
                        />
                    </div>
                    <p className="authP">{errors?.password?.message}</p>
                    <button className="fluid ui button blue">Sign In</button>
                </div>
            </form>

            <a className="signInUpFooter" href={TELEGRAM_BOT} target="_blank" rel="noreferrer">
                Not on Notiprice yet? Type to our <u>Telegram bot</u>
            </a>
        </div>
    );
}

export default SignIn;
