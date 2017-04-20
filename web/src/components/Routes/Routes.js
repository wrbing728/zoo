'use strict';

import App from '../../layouts/App';
import CommonMixin from '../../mixins/CommonMixin';
import Loading from '../../pages/Loading/Loading';
import NotFound from '../../pages/NotFound/NotFound';

const {React, Component, PropTypes, Router, Route, IndexRedirect, connect, reactMixin} = window._external;

@reactMixin.decorate(CommonMixin)
class Routes extends Component {

    static history = null;

    static replace(config) {
        const {history} = Routes;
        history && history.replace(config);
    }

    static goBack() {
        const {history} = Routes;
        history && history.goBack();
    }

    static goto(config) {
        const {history} = Routes;
        history && history.push(config);
    }

    init() {
        const {history} = this.props;
        Routes.history = history;
    }

    destroy() {
        Routes.history = null;
    }

    render() {
        const {history} = this.props;
        return (
            <Router history={history}>
                <Route path="/" component={App}>
                    <IndexRedirect to="loading"/>
                    <Route path="loading" component={Loading}/>
                    <Route path="*" component={NotFound}/>
                </Route>
            </Router>
        );
    }
}

Routes.propTypes = {
    history: PropTypes.object.isRequired
};

export {Routes};

export default connect(state => ({}))(Routes);
